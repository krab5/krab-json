package org.krab.json.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.krab.json.JsonFactory;
import org.krab.json.JsonObject;
import org.krab.json.util.JsonObjectBuilder;

public class TestJsonExportManual {
	public static void main(String[] args) throws IOException {
		JsonObjectBuilder job = new JsonObjectBuilder(JsonFactory.DEFAULT_FACTORY);
		job.add("abc", 15)
		   .addArray("def", ab -> ab
			   .add(true)
			   .add("test")
			   .add(12.5)
		   )
		   .addObject("ghi", ob -> ob
			   .add("123", "test \n test")
			   .add("\\jkl", true)
			   .addArray("array", ab -> ab
					.add("a")
					.add("\"")
					.addArray(ab2 -> ab2
						.add(true)
						.add(false)
					)
			   )
			);
		JsonObject value = job.build();
		
		Writer writer = new OutputStreamWriter(System.out);
		writer.append("begin...\n");
		writer.flush();
		JsonExporter exporter = new JsonExporter(writer, value);
		exporter.setStyle(JsonExporter.Style.SPACED);
		exporter.export();
		
		System.out.println("done.");
	}
}
