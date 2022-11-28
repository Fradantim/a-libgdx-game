package net.fradantim.platformertutorial.world.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TMXMapLoader {

	private enum TMXXMLNames {
		MAP("map"), HEIGHT("height"), WIDTH("width"), TILE_WIDTH("tilewidth"), TILE_HEIGHT("tileheight"),
		LAYER("layer"), DATA("data"), ENCODING("encoding");

		private String id;

		private TMXXMLNames(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}

	private static final String CSV_DELIMITER = ",";
	private static final String CSV_NEW_LINE = "\n";
	private static final String ENCODING_CSV = "csv";

	public static GameMap loadMap(String name) {
		return null;
	}

	public GameMap generateFromTMX(String xmlFileName) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(getClass().getClassLoader().getResourceAsStream(xmlFileName));
		Node mapNode = document.getElementsByTagName(TMXXMLNames.MAP.getId()).item(0);

		NamedNodeMap namedNode = mapNode.getAttributes();
		int height = Integer.parseInt(namedNode.getNamedItem(TMXXMLNames.HEIGHT.getId()).getNodeValue());
		int width = Integer.parseInt(namedNode.getNamedItem(TMXXMLNames.WIDTH.getId()).getNodeValue());

		NodeList mapNodeChilds = mapNode.getChildNodes();

		List<int[][]> layersAsList = new ArrayList<>();

		for (int i = 0; i < mapNodeChilds.getLength(); i++) {
			Node mapNodeChild = mapNodeChilds.item(i);
			if (mapNodeChild.getNodeName().equals(TMXXMLNames.LAYER.getId())) {

				NodeList layerNodeChilds = mapNodeChild.getChildNodes();
				for (int j = 0; j < layerNodeChilds.getLength(); j++) {
					Node layerNodeChild = layerNodeChilds.item(j);
					if (layerNodeChild.getNodeName().equals(TMXXMLNames.DATA.getId())) {
						String encoding = layerNodeChild.getAttributes().getNamedItem(TMXXMLNames.ENCODING.getId())
								.getNodeValue();
						switch (encoding) {
						case (ENCODING_CSV):
							int[][] layer = new int[height][width];
							layersAsList.add(fillLayerWithCSV(new int[height][width], layerNodeChild.getTextContent()));
							break;
						default:
							throw new RuntimeException("Encoding '" + encoding + "' unknown.");
						}
					}
				}
			}
		}

		int[][][] layers = layersAsList.toArray(new int[layersAsList.size()][][]);
		int tileWidth = Integer.parseInt(namedNode.getNamedItem(TMXXMLNames.TILE_WIDTH.getId()).getNodeValue());
		int tileHeight = Integer.parseInt(namedNode.getNamedItem(TMXXMLNames.TILE_HEIGHT.getId()).getNodeValue());
		
		return new GameMap(layers, tileHeight, tileWidth); 
	}

	private int[][] fillLayerWithCSV(int[][] layer, String csv) {
		int rowNum = layer.length - 1; // cargo las filas de abajo hacia arriba

		for (String csvLine : csv.split(CSV_NEW_LINE)) {
			if (!csvLine.trim().isEmpty()) {
				String[] charCols = csvLine.split(CSV_DELIMITER);
				for (int i = 0; i < charCols.length; i++) {
					layer[rowNum][i] = Integer.parseInt(charCols[i]);
				}
				rowNum--;
			}
		}

		return layer;
	}
}
