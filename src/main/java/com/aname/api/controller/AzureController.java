package com.aname.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.aname.api.service.AzureBlobService;
import com.aname.api.service.to.DocResponseDTO;

@RestController
@RequestMapping("/files")
public class AzureController {

	@Autowired
	private AzureBlobService azureBlobAdapter;

	/**
	* En el caso de los contenidos, el contenedor deberá conservar el archivo.
	* @param file - El archivo
	* @param contenedor - El nombre del contenedor
	* @param email - Clave primario que se liga con el archivo
	* @return Respuesta ResponseEntity que comprueba si el archivo se puede subir
	*/
	@PostMapping
	public ResponseEntity<?> upload(@RequestParam MultipartFile file, @RequestParam String contenedor,
			@RequestParam String email) {
		try {
			validateFileSize(file, contenedor);

			DocResponseDTO doc = azureBlobAdapter.upload(file, contenedor, email);
			return ResponseEntity.ok(doc);
		} catch (MaxUploadSizeExceededException e) {
			return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
					.body("El tamaño del archivo excede el límite permitido: " + formatSize(e.getMaxUploadSize()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al cargar documento: " + e.getMessage());
		}
	}

	/**
	* Valida el tamaño del archivo. Arroja MaxUploadSizeExceededException si el archivo es mayor que maxSizeBytes
	* @param file - MultipartFile para ser validado.
	* @param contenedor - Nombre del contenedor de archivos
	*/
	private void validateFileSize(MultipartFile file, String contenedor) {
		long maxSizeBytes = (contenedor.equals("fotografia")) ? 1 * 1024 * 1024 : 2 * 1024 * 1024;
		// si el tamaño del archivo es mayor que maxSizeBytes
		if (file.getSize() > maxSizeBytes) {
			throw new MaxUploadSizeExceededException(maxSizeBytes);
		}
	}


	/**
	* Formatos en bytes para un formato legible por el hombre. Esto se utiliza para mostrar el tamaño de un archivo en la barra
	* @param sizeInBytes - el tamaño en bytes para formatar
	* @return el tamaño en formato legible para el nombre con 2 decimales por kilobytes para el tamaño de archivo
	*/
	private String formatSize(long sizeInBytes) {
		double sizeInKb = (double) sizeInBytes / (1024 * 1024);
		return String.format("%.2f MB", sizeInKb);
	}


	/**
	* Elimina un archivo del almacenamiento. Esta es una operación asincrona. Para comprobar si el archivo está presente, use
	* @param fileName - Nombre del archivo a eliminar.
	* @return Una entidad de respuesta con estado 200 (OK) y cuerpo que indique éxito o fracaso (con cuerpo).
	*/
	@DeleteMapping
	public ResponseEntity<Boolean> delete(@RequestParam String fileName) {
		azureBlobAdapter.deleteBlob(fileName);
		return ResponseEntity.ok().build();
	}
}
