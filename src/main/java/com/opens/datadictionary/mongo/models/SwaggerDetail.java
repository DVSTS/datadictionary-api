package com.opens.datadictionary.mongo.models;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.models.Swagger;

@Document(collection = "swaggerdetail")
public class SwaggerDetail implements Serializable {

	public SwaggerDetail(String id, String fileName, String content, String uploadDate, String version, String name,
			Swagger swagger) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.content = content;
		this.uploadDate = uploadDate;
		this.version = version;
		this.name = name;
		this.swagger = swagger;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private static final long serialVersionUID = 9148030041953822710L;

	private String id;

	private String fileName;

	@JsonIgnore
	private String content;

	private String uploadDate;

	private String version;

	private String name;

	private Swagger swagger;

	public Swagger getSwagger() {
		return swagger;
	}

	public void setSwagger(Swagger swagger) {
		this.swagger = swagger;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public SwaggerDetail() {

	}

	public SwaggerDetail(String id, String fileName, String content) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
