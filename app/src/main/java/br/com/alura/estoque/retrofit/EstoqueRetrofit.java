package br.com.alura.estoque.retrofit;

import org.jetbrains.annotations.NotNull;

import br.com.alura.estoque.retrofit.service.ProdutoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EstoqueRetrofit {
    private final String HOST = "http://192.168.0.103:8080/";
    private final ProdutoService produtoService;

    public EstoqueRetrofit() {


        OkHttpClient client = configuraClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        produtoService = retrofit.create(ProdutoService.class);
    }

    @NotNull
    private OkHttpClient configuraClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .build();
    }

    public ProdutoService getProdutoService(){
        return produtoService;
    }
}
