package br.com.alura.screenmatch.services;

public interface IDataConverter {
    <T> T getData(String json, Class<T> clazz);
}