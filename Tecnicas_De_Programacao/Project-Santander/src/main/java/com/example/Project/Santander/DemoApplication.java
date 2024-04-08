package com.example.Project.Santander;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {


	private final JdbcTemplate jdbcTemplate;

	public DemoApplication(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		List<Produto> produtos = openCsv();
		countCategorias(produtos);
		valorMedio(produtos);
		estoqueBaixo(produtos);
	}

	private static List<Produto> openCsv() throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get("lista_de_produtos.csv"));
		CsvToBean<Produto> csvToBean =
				new CsvToBeanBuilder<Produto>(reader)
						.withType(Produto.class)
						.withIgnoreLeadingWhiteSpace(true)
						.build();
		return csvToBean.parse();
	}

	private static int countCategorias(List<Produto> lista) {
		List<String> novaLista = new ArrayList<>();
		lista.forEach(produto ->
		{
			if (!novaLista.contains(produto.getCategoria())){
				novaLista.add(produto.getCategoria());
			}
		});
		System.out.println("Foram encontradas " + novaLista.size() + " categorias.");
		return novaLista.size();
	}

	private static BigDecimal valorMedio(List<Produto> lista) {
		BigDecimal media = BigDecimal.ZERO;
		for (Produto produto : lista) {
			media = media.add(produto.getPrice());
		}
		BigDecimal resultado = media.divide(BigDecimal.valueOf(lista.size()));
		System.out.println("A média de preços é R$: " + resultado);
		return media.divide(resultado);
	}

	private static List<Produto> estoqueBaixo(List<Produto> lista) {
		List<Produto> novaLista = new ArrayList<>();
		lista.forEach(produto ->
		{
			if (produto.getQuantidade() <= 3){
				novaLista.add(produto);
			}
		});
		System.out.println("Estoque baixo dos seguintes produtos:");
		novaLista.forEach(produto -> System.out.println("-"+produto.getNome() +":"+ produto.getQuantidade()));
		return novaLista;
	}

}
