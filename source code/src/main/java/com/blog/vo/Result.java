package com.blog.vo;

public class Result {
	// Perbaikan: Mengubah nama variabel dari 'result' menjadi 'code'
	int code;
	String message;

	public Result() {
	}

	public Result(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}