package br.com.alura.estoque.retrofit;

import br.com.alura.estoque.retrofit.service.ProdutoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EstoqueRetrofit {
    private final String HOST = "http://192.168.0.103:8080/";
    private final ProdutoService produtoService;

    public EstoqueRetrofit() {


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client  = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        produtoService = retrofit.create(ProdutoService.class);
    }

    public ProdutoService getProdutoService(){
        return produtoService;
    }
}
