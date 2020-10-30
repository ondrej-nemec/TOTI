package mvc.response;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import common.exceptions.LogicException;
import core.text.Text;
import mvc.ResponseHeaders;
import mvc.templating.TemplateFactory;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public class PdfResponse implements Response {
	
	private final Response response;

	public PdfResponse(Response response) {
		if (response instanceof FileResponse) {
			throw new LogicException("File cannot be returned as PDF");
		}
		if (response instanceof RedirectResponse) {
			throw new LogicException("Redirect cannot be returned as PDF");
		}
		if (response instanceof JsonResponse) {
			throw new LogicException("Json cannot be returned as PDF");
		}
		this.response = response;
	}

	@Override
	public void addParam(String name, Object value) {}

	@Override
	public RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String charset) {
		RestApiResponse res = response.getResponse(header, templateFactory, translator, charset);
		return RestApiResponse.binaryResponse(res.getStatusCode(), res.getHeader(), (bout)->{
			StringWriter writer = new StringWriter();
			BufferedWriter bw = new BufferedWriter(writer);
			res.createTextContent(bw);
			
			Document document = new Document();
		    Text.read((br)->{
		    	PdfWriter pdfWriter;
				try {
					pdfWriter = PdfWriter.getInstance(document, bout);
				} catch (DocumentException e) {
					throw new RuntimeException(e);
				}
		    	document.open();
			    XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, br);
			    document.close();
		    	return null;
		    }, new ByteArrayInputStream(writer.toString().getBytes()));
		});
	}


}
