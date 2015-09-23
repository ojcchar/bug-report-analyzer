package org.seers.bugrepanalyzer.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;

public class HttpJiraUtils {
	private final static HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

	public static String getJql(String project, int i, int numResults) throws UnsupportedEncodingException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("jql=");
		stringBuilder.append(URLEncoder.encode("project=" + project, "UTF-8"));
		stringBuilder.append("&");
		stringBuilder.append("startAt=" + URLEncoder.encode(i + "", "UTF-8"));
		stringBuilder.append("&");
		stringBuilder.append("maxResults=" + URLEncoder.encode(numResults + "", "UTF-8"));
		return stringBuilder.toString();
	}

	public static String getStringResponse(String urlJira) throws IOException {

		URL url = new URL(urlJira);

		HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(url));
		HttpResponse response = httpRequest.execute();

		InputStream stream = response.getContent();

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		return result.toString();

	}
}
