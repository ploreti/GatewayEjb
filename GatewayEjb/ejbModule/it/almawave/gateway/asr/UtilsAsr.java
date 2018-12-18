package it.almawave.gateway.asr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.pervoice.audiomabox.services.common._1.ClientInfoType;

public class UtilsAsr {
	
	
	/*
	 * metodo per popolare il clientInfo per tutte le chiate ai servi asr
	 */
	public static ClientInfoType popolaclientInfo() {
		
		ClientInfoType clientInfo = new ClientInfoType();
		
		clientInfo.setHostname("RFI-LAB-WE-INF-VM-IRI-GW-001");
		clientInfo.setRegistrationCode("bb9fbdae-020f-11e9-bc18-000d3a4561eb");
		clientInfo.setProductName("RFIVisiteInLinea");
		clientInfo.setProductVersion("1.0.0");
		
		return clientInfo;
		
	}
	
	/*
	 * converte il file pvt in document
	 */
	public static Document convertStringToDocument(String filePvt) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(filePvt.replaceAll("\n|\r", ""))));
		return doc;
	}
	
	/*
	 * crea la stringa testo dal document del file pvt
	 */
	public static String concatena(Document fileXml) {
		String testo = "";
		NodeList nList = fileXml.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			NodeList mList = nNode.getChildNodes();
			for (int temp1 = 0; temp1 < mList.getLength(); temp1++) {
				Node mNode = mList.item(temp1);
				if ("Episode".equalsIgnoreCase(mNode.getNodeName())) {
					NodeList lList = mNode.getChildNodes();
					for (int temp2 = 0; temp2 < lList.getLength(); temp2++) {
						Node lNode = lList.item(temp2);
						if ("Section".equalsIgnoreCase(lNode.getNodeName())) {
							NodeList sList = lNode.getChildNodes();
							for (int temp3 = 0; temp3 < sList.getLength(); temp3++) {
								Node sNode = sList.item(temp3);
								if ("Turn".equalsIgnoreCase(sNode.getNodeName())) {
									if (sNode.getNodeType() == Node.ELEMENT_NODE) {
										Element sElement = (Element) sNode;
										String speaker = sElement.getAttribute("speaker");
										//per il cambio speaker, per il nostro applicativo dovà risultare solo un speaker
										//testo += "\n" + speaker + ":";
										NodeList tList = sNode.getChildNodes();
										for (int temp4 = 0; temp4 < tList.getLength(); temp4++) {
											Node tNode = tList.item(temp4);
											if ("Phrase".equalsIgnoreCase(tNode.getNodeName())) {
												NodeList pList = tNode.getChildNodes();
												for (int temp5 = 0; temp5 < pList.getLength(); temp5++) {
													Node pNode = pList.item(temp5);
													if ("Token".equalsIgnoreCase(pNode.getNodeName())) {
														if (pNode.getNodeType() == Node.ELEMENT_NODE) {
															Element eElement = (Element) pNode;
															//LOGGER.info("_______ data : " + eElement.getAttribute("data"));
															testo += eElement.getAttribute("data") + " ";
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return testo;
	}
	
	public static void main(String[] args) {



		BufferedReader br = null;
		FileReader fr = null;

		String FILENAME = "C:\\Users\\Viviana OS\\Documents\\ALMAWAVE\\pvt\\20170731_122924_2150634_0_0.pvt";

		try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);
			
			String filePvt = "";
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				filePvt += sCurrentLine;
			}

			Document fileXml = UtilsAsr.convertStringToDocument(filePvt);
			fileXml.getDocumentElement().normalize();

			String testo = UtilsAsr.concatena(fileXml);
			
			System.out.println(testo);

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		
	}

}
