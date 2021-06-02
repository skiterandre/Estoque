package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProdutoRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private static final String MENSAGEM_ERRO_BUSCA_PRODUTOS = "Não foi possível carregar produtos novos:";
    private static final String MENSAGEM_ERRO_REMOCAO_PRODUTO = "Produto não pode ser removido ";
    private static final String MENSAGEM_ERRO_SALVA_PRODUTO = "Não foi possível salvar o produto: ";
    private static final String MENSAGEM_ERRO_EDICAO_PRODUTO = "Não foi possivel editar o produto:";
    private ListaProdutosAdapter adapter;

    private ProdutoRepository produtoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();
        produtoRepository = new ProdutoRepository(this);
        buscaProdutos();
    }

    private void buscaProdutos() {

        produtoRepository.buscaProdutos(new ProdutoRepository.DadosCarregadosCallback<List<Produto>>() {
            @Override
            public void onSuccess(List<Produto> resultado) {
                adapter.atualiza(resultado);
            }

            @Override
            public void onError(String erro) {
                mostraErro(MENSAGEM_ERRO_BUSCA_PRODUTOS + erro);
            }
        });
    }


    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener(this::remove);
    }

    private void remove(int posicao, Produto produtoEscolhido) {
        produtoRepository.remove(produtoEscolhido, new ProdutoRepository.DadosCarregadosCallback<Void>() {
            @Override
            public void onSuccess(Void resultado) {
                adapter.remove(posicao);
            }

            @Override
            public void onError(String erro) {
                mostraErro(MENSAGEM_ERRO_REMOCAO_PRODUTO + erro);
            }
        });
    }

    private void mostraErro(String mensagem) {
        Toast.makeText(ListaProdutosActivity.this, mensagem, Toast.LENGTH_SHORT).show();
    }


    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, this::salva).mostra();
    }

    private void salva(Produto produtoCriado) {
        produtoRepository.salva(produtoCriado, new ProdutoRepository.DadosCarregadosCallback<Produto>() {
            @Override
            public void onSuccess(Produto resultado) {
                adapter.adiciona(resultado);
            }

            @Override
            public void onError(String erro) {
                mostraErro(MENSAGEM_ERRO_SALVA_PRODUTO + erro);
            }
        });
    }


    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoEditado -> edita(posicao, produtoEditado))
                .mostra();
    }

    private void edita(int posicao, Produto produtoEditado) {
        produtoRepository.edita(produtoEditado, new ProdutoRepository.DadosCarregadosCallback<Produto>() {
            @Override
            public void onSuccess(Produto produto) {
                adapter.edita(posicao, produto);
            }

            @Override
            public void onError(String erro) {
                mostraErro(MENSAGEM_ERRO_EDICAO_PRODUTO + erro);
            }
        });
    }

}
