package com.tiagoamp.sjc.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

	public void saveMultipartFileInFileSystem(MultipartFile mfile, String filepath) throws IllegalStateException, IOException {
		File dest = new File(filepath);
		mfile.transferTo(dest);
	}
	
	public long getNumberOfFilesInUploadDirectory(Path uploadDir) throws IOException {
		return Files.list(uploadDir).count();
	}
	
	public List<Path> getUploadedFilesPath(Path uploadDir) throws IOException {
		return Files.list(uploadDir).collect(Collectors.toList());
	}
	
	public void cleanDirectory(Path uploadDir) throws IOException {
		Files.newDirectoryStream(uploadDir).forEach( f -> {
			try {
				Files.delete(f);
			} catch (IOException e) {
				LOGGER.debug("Error on cleaning directories.", e);
			}
		});
	}
	
	public Optional<Path> findAfastamentoSpreadsheetPath(Path uploadDir) {
		Optional<Path> result = Optional.empty();
		try {
			result = Files.list(uploadDir)
					.filter(path -> !Files.isDirectory(path))
					.filter(path -> path.getFileName().toString().toLowerCase().contains("afastamento"))
					.filter(path -> path.getFileName().toString().toLowerCase().endsWith("xlsx"))
					.findFirst();
		} catch (IOException e) {
			LOGGER.debug("Error in searching 'afastamentos' spreadsheet.", e);
		}
		return result;
	}
	
}
