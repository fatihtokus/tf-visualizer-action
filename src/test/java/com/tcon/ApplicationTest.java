package com.tcon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

public class Application {
	private static final Logger logger = LogManager.getLogger(Application.class);
	public static String PULLREQUEST;
	public static String TOKEN;
	public static void main(String[] args) throws Exception{
		logger.error("Plugin started with {} on {} at: {}", Arrays.toString(args), operatingSystem(), Instant.now());

		if(args.length != 2) {
			throw new RuntimeException("Expected argument number is 2 but " + args.length + " supplied!");
		}

/*		listDirectory("/");
		listDirectory("");
		listDirectory("/home/runner/work/tf-visualizer-action-test/tf-visualizer-action-test");
		listDirectory("home/runner/work/tf-visualizer-action-test/tf-visualizer-action-test");
		listDirectory("/github/workspace");
		listDirectory("github/workspace");*/

		//listDirectory("/github/workspace/results.json");

		PULLREQUEST = args[0];
		TOKEN = args[1];
		Application bootApplication = new Application();
		Process process = bootApplication.addCommentPR();
	}

	private static void listDirectory(String path) {
		Path directory = Paths.get(path);
		File file1 = directory.toFile();
		logger.error("Looking at: {}, {}", path, file1.exists());

		try {
			String all =
					Files.walk(directory)
							.map(Path::toFile)
							.map(file -> file.getName())
							.collect(Collectors.joining(", "));
			logger.error(path + ": " +all);

		} catch (Exception e) {
			logger.error(path, e);
		}
	}



	public String visualize(String path) throws Exception {
		File file1 = Paths.get(path).toFile();
		Gson gson = new Gson();
		JsonObject resultObject = gson.fromJson(new FileReader(path), JsonObject.class);
		JsonArray results = resultObject.get("results").getAsJsonArray();

		return "";

	}

	public Process addCommentPR() throws Exception {
		// "https://api.github.com/repos/fatihtokus/tf-visualizer-action-test/pulls/2";
		// "https://api.github.com/repos/fatihtokus/tf-visualizer-action-test/issues/2/comments";
		String url = PULLREQUEST.replace("/pulls/", "/issues/") + "/comments";
		//String body = "test";
		String payload = String.format(" \" { \\\"body\\\" : \\\" %s \\\" } \" ", body);
		//String credentials = "-u " + TOKEN;
		String credentials = String.format("--header \"authorization: Bearer %s\"", TOKEN);
		payload = payload.isEmpty() ? "" : "-d " + payload;
		String curlCommand = String.format("curl %s %s %s", url, credentials, payload);
		List<String> shellCommand = isRunningOnWindows() ? asList("cmd", "/C", curlCommand) : asList("bash", "-c", curlCommand);
		logger.error("Requesting '{}'", shellCommand);
		ProcessBuilder builder = new ProcessBuilder(shellCommand);
		Process process = builder.start();
		String response = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(joining("\n"));
		logger.error("Requested '{}'", shellCommand);
		logger.error("Response '{}'", response);
		return process;
	}
	private static String operatingSystem() {
		return System.getProperty("os.name");
	}

	private boolean isRunningOnWindows() {
		return operatingSystem().toLowerCase().contains("windows");
	}
}