package com.aname.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@Configuration
public class AzureBlobConfig {

	@Value("${azure.storage.connection.string}")
	private String connectionString;

	@Value("${azure.storage.container.name}")
	private String containerName;

	/*
	* Método que crea un cliente BlobServiceClient.
	* @return un cliente BlobServiceClient usado para requerimientos/respuesta a las llamadas al servicio. 
	* Se crea bajo demanda
	*/
	@Bean
	public BlobServiceClient clobServiceClient() {

		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient();

		return blobServiceClient;

	}

	/*
	* Metodo que crea y retorna un BlobContainerClient para el contenedor. El contenedor ya debe estar creado.
	* @return BlobContainerClient para el contenedor.
	*/
	@Bean
	public BlobContainerClient blobContainerClient() {

		BlobContainerClient blobContainerClient = clobServiceClient().getBlobContainerClient(containerName);

		return blobContainerClient;

	}

}