package net.fradantim.platformertutorial.world.map;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.fradantim.platformertutorial.world.map.GameMap.Layer;
import net.fradantim.platformertutorial.world.map.GameMap.Row;

public class TMXMapLoader {
	
	private enum TMXXMLNames{
		MAP("map"),
		HEIGHT("height"),
		WIDTH("width"),
		TILE_WIDTH("tilewidth"),
		TILE_HEIGHT("tileheight"),
		LAYER("layer"),
		DATA("data"),
		ENCODING("encoding");
		
		private String id;
		private TMXXMLNames (String id) {
			this.id=id;
		}
		public String getId() { return id;}
	}

	private static final String CSV_DELIMITER = ",";
	private static final String CSV_NEW_LINE = "\n";
	private static final String ENCODING_CSV = "csv";
		
	public static GameMap loadMap (String name) {
		return null;
	}
	
	public GameMap generateFromTMX(String xmlFileName) throws ParserConfigurationException, SAXException, IOException {
		GameMap map = new GameMap();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(getClass().getClassLoader().getResourceAsStream(xmlFileName));
		Node mapNode = document.getElementsByTagName(TMXXMLNames.MAP.getId()).item(0);
		
		NamedNodeMap namedNode = mapNode.getAttributes();
		map.setHeight(Integer.parseInt(namedNode.getNamedItem(TMXXMLNames.HEIGHT.getId()).getNodeValue()));
		map.setWidth(Integer.parseInt(namedNode.getNamedItem(TMXXMLNames.WIDTH.getId()).getNodeValue()));
		map.setTilePixelWidth(Integer.parseInt(namedNode.getNamedItem(TMXXMLNames.TILE_WIDTH.getId()).getNodeValue()));
		map.setTilePixelHeight(Integer.parseInt(namedNode.getNamedItem(TMXXMLNames.TILE_HEIGHT.getId()).getNodeValue()));
		
		NodeList mapNodeChilds = mapNode.getChildNodes();
		
		for(int i=0; i< mapNodeChilds.getLength(); i++) {
			Node mapNodeChild = mapNodeChilds.item(i);
			if(mapNodeChild.getNodeName().equals(TMXXMLNames.LAYER.getId())) {
				
				NodeList layerNodeChilds = mapNodeChild.getChildNodes();
				for(int j=0; j< layerNodeChilds.getLength(); j++) {
					Node layerNodeChild = layerNodeChilds.item(j);
					if(layerNodeChild.getNodeName().equals(TMXXMLNames.DATA.getId())) {
						switch(layerNodeChild.getAttributes().getNamedItem(TMXXMLNames.ENCODING.getId()).getNodeValue()) {
						case (ENCODING_CSV):
							map.addLayer(getLayerFromCSV(map,layerNodeChild.getTextContent()));
							break;
						default:
							break;
						}
					}
				}
			}
		}
		
		return map;
	}
	
	private Layer getLayerFromCSV (GameMap map, String CSVContent) {
		Layer layer = map.new Layer();
		for(String CSVLine : CSVContent.split(CSV_NEW_LINE)) {
			//la 1ra linea suele venir vacia
			if(!CSVLine.trim().isEmpty()) {
				Row row = map.new Row();
				for(String CSVField : CSVLine.split(CSV_DELIMITER)) {
					row.addColumn(map.new Column(Integer.parseInt(CSVField)));
				}
				layer.addRow(row);
			} 
		}
		Collections.reverse(layer.getRows());
		return layer;
	}	
}
