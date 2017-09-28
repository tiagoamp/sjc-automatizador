package com.tiagoamp.sjc.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

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
	
	public void cleanUploadDirectory(Path uploadDir) throws IOException {
		Files.newDirectoryStream(uploadDir).forEach( f -> {
			try {
				Files.delete(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
}
