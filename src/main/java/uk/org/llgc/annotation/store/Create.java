package uk.org.llgc.annotation.store;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.File;

import java.util.Map;

import com.github.jsonldjava.utils.JsonUtils;

import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

public class Create extends HttpServlet {
	protected AnnotationUtils _annotationUtils = null;
	protected StoreAdapter _store = null;

	public void init(final ServletConfig pConfig) throws ServletException {
		super.init(pConfig);
		Dataset tDataset = TDBFactory.createDataset(super.getServletContext().getInitParameter("data_dir"));
		_annotationUtils = new AnnotationUtils(new File(super.getServletContext().getRealPath("contexts")));
		_store = new StoreAdapter(tDataset);
	}

	public void doPost(final HttpServletRequest pReq, final HttpServletResponse pRes) throws IOException {
		Map<String, Object> tAnnotationJSON = _annotationUtils.readAnnotaion(pReq.getInputStream()); 
		/**/System.out.println("JSON in:");
		/**/System.out.println(JsonUtils.toPrettyString(tAnnotationJSON));

		Model tModel = _store.addAnnotation(tAnnotationJSON);

		Map<String, Object> tAnnotationList = _annotationUtils.createAnnotationList(tModel);

		pRes.setStatus(HttpServletResponse.SC_CREATED);
		pRes.setContentType("application/ld+json");
		/**/System.out.println("JSON out:");
		/**/System.out.println(JsonUtils.toPrettyString(tAnnotationList));
		pRes.getOutputStream().println(JsonUtils.toPrettyString(tAnnotationList));
	}
}
