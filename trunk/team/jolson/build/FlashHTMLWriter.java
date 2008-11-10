/*
 * Generation of HTML to wrap Flash simulations in.
 * Automatically handles inserting internationalization XML and other data
 *
 * author: Jonathan Olson
 * created: 11/8/2008
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FlashHTMLWriter {
	public static void writeHTML(String simName, String language, String xmlFile, String htmlFile, String propertiesFile) throws FileNotFoundException, UnsupportedEncodingException {
		/* Reads internationaliaztion data from an XML file, and generates the corresponding HTML file
		 * that will pass the data into Flash through FlashVars parameters.
		 *
		 * simName: example: "pendulum-lab"
		 * language: example: "en"
		 * xmlFile: The file to read internationalization data from. Example: "pendulum-lab-strings_en.xml"
		 * htmlFile: File to output the HTML into.
		 * propertiesFile: example: "pendulum-lab.properties". Includes version information and background color
		 */
		
		// all of the fields from the <simName>.properties file
		String versionMajor = null;
		String versionMinor = null;
		String dev = null;
		String revision = null;
		String bgcolor = null;
		
		// parse the .properties file, store results in variables above
		File propFile = new File(propertiesFile);
		Scanner propScanner = new Scanner(propFile);
		propScanner.useDelimiter("[\n=]");
		while (propScanner.hasNext()) {
			String field = propScanner.next();
			String value = propScanner.next().trim();
			if(field.equals("version.major")) {
				versionMajor = value;
			} else if(field.equals("version.minor")) {
				versionMinor = value;
			} else if(field.equals("version.dev")) {
				dev = value;
			} else if(field.equals("version.revision")) {
				revision = value;
			} else if(field.equals("bgcolor")) {
				bgcolor = value;
			}
		}
		propScanner.close();
		
		// open and read all internationalization data from XML file
		File inFile = new File(xmlFile);
		Scanner scan = new Scanner(inFile);
		scan.useDelimiter("\\Z");
		String rawXML = scan.next();
		
		// encode XML into UTF-8 form compatible for passing into Flash
		String encodedXML = URLEncoder.encode(rawXML, "UTF-8");
		
		// prepare variables to be passed in
		String flashVars = "countryCode=" + language;
		flashVars += "&internationalization=" + encodedXML;
		flashVars += "&versionMajor=" + versionMajor;
		flashVars += "&versionMinor=" + versionMinor;
		flashVars += "&dev=" + dev;
		flashVars += "&revision=" + revision;
		flashVars += "&simName=" + simName;
		
		// prepare string of HTML file:
		String swfName = simName + ".swf";
		String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n";
		html += "<head>\n";
		html += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n";
		html += "<title>" + simName + "_" + language + "</title>\n"; // NOTE: change title? usually not seen.
		
		// use JavaScript with ExternalInterface to detect window closing on those browsers
		html += "<SCRIPT LANGUAGE=\"JavaScript\">\n";
		html += "window.onbeforeunload = clean_up;\n";
		html += "function clean_up() {\n";
		html += "var sim = document[\"" + simName + "\"] || window[\"" + simName + "\"];\n";
		html += "sim.beforeClose();\n";
		html += "}\n";
		html += "</SCRIPT>\n";
		
		html += "</head>\n";
		html += "<body bgcolor=\"" + bgcolor + "\">\n"; // we want to get the correct background color!
		html += "<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0\" width=\"100%\" height=\"100%\" id=\"" + simName + "\" align=\"middle\">\n";
		html += "<param name=\"allowScriptAccess\" value=\"sameDomain\" />\n";
		html += "<param name=\"movie\" value=\"" + swfName + "\" />\n";
		html += "<param name=\"quality\" value=\"high\" />\n";
		html += "<param name=\"bgcolor\" value=\"" + bgcolor + "\" />\n"; // we want to get the correct background color!
		html += "<param name = \"FlashVars\"  value = \"" + flashVars + "\"/>\n";
		html += "<embed id=\"x_" + simName + "\" src=\"" + swfName + "\" quality=\"high\" bgcolor=\"" + bgcolor + "\" width=\"100%\" height=\"100%\" name=\"" + simName + "\" align=\"middle\" allowScriptAccess=\"sameDomain\" FlashVars = \"" + flashVars + "\" ";
		html += "type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" />\n";
		html += "</object>";
		html += "</body>";
		html += "</html>";
		
		// write to file
		FileOutputStream fileOut = new FileOutputStream(htmlFile);
		PrintStream printOut = new PrintStream(fileOut);
		printOut.println(html);
		printOut.close();
	}
	public static void main(String args[]) {
		try {
			String simName = "pendulum-lab";
			String language = "sk";
			if(args.length > 1) {
				simName = args[0];
				language = args[1];
			}
			
			// relative pathnames?
			String xmlFile = simName + "-strings_" + language + ".xml";
			String htmlFile = simName + "_" + language + ".html";
			String propertiesFile = simName + ".properties";
			
			writeHTML(simName, language, xmlFile, htmlFile, propertiesFile);
		} catch(FileNotFoundException e) {
			System.out.println("File Not Found: " + e.toString());
		} catch(UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding");
		}
	}
}
