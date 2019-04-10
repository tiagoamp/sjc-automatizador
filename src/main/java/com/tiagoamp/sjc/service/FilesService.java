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
public class FilesService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FilesService.class);

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
	
	public void cleanDirectory(Path dir) throws IOException {
		Files.newDirectoryStream(dir).forEach( f -> {
			try {
				Files.delete(f);
			} catch (IOException e) {
				LOGGER.debug("Error on cleaning directories.", e);
			}
		});
	}
	
	public void cleanDirectories(Path... dirs) throws IOException {
		for (Path  dir : dirs) {
			cleanDirectory(dir);
		}
	}
	
	public void createDirectories(Path... paths) throws IOException {
		try {
			for (int i = 0; i < paths.length; i++) {
				if (Files.notExists(paths[i])) {
					Files.createDirectories(paths[i]);
				}
			}	
		} catch (IOException e) {
			LOGGER.error("Directory access error", e);
			System.out.println("Erro ao acessar diretórios!!!");			
			throw e;			
		}	
	}
	
	public Optional<Path> findAfastamentoSpreadsheetPath(Path dir) {
		Optional<Path> result = Optional.empty();
		try {
			result = Files.list(dir)
					.filter(path -> !Files.isDirectory(path))
					    // .filter(path -> path.getFileName().toString().toLowerCase().contains(AFASTAMENTO_IDENTIFIED_FILE_NAME))
					.filter(path -> path.getFileName().toString().toLowerCase().endsWith("xlsx") || 
									path.getFileName().toString().toLowerCase().endsWith("xls"))
					.findFirst();
		} catch (IOException e) {
			LOGGER.debug("Error in searching 'afastamentos' spreadsheet.", e);
		}
		return result;
	}
	
	public void deleteAfastamentoSpreadsheet(Path dir) {
		Optional<Path> path = findAfastamentoSpreadsheetPath(dir);
		path.ifPresent(p -> {
			try {
				Files.delete(p);
			} catch (IOException e) {
				LOGGER.debug("Error in deleting 'afastamentos' spreadsheet.", e);
			}
		});		
	}
	
}
