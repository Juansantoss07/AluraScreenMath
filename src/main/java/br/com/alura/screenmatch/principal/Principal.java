package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série pelo título
                    5 - Buscar série por ator(a)
                    6 - Exibir TOP 5 séries
                    7 - Buscar série por categoria
                    8 - Buscar séries com limite de temporada definido
                                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePeloTitulo();
                    break;
                case 5:
                    buscarSériesPorAtor();
                    break;
                case 6:
                    vizualizarTop5();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    buscarSerieComLimiteDeTemporada();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série que deseja ver os epsódios");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpsodios(episodios);
            repositorio.save(serieEncontrada);
            }else {
            System.out.println("Série não encontrada.");
        }
    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePeloTitulo() {
        System.out.println("Escolha uma série pelo título:");
        var serieTitulo = leitura.nextLine();

        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(serieTitulo);

        if (serieBuscada.isPresent()){
            System.out.println("Dados da série: " + serieBuscada);
        } else {
            System.out.println("Série não localizada.");
        }
    }

    private void buscarSériesPorAtor(){
        System.out.println("Digite o nome do ator que deseja ver as suas séries:");
        var atorBuscado = leitura.nextLine();
        System.out.println("Deseja ver as séries a partir de qual avaliação?");
        var avaliacaoBuscado = leitura.nextDouble();
        List<Serie> seriesQueContemAtor = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(atorBuscado, avaliacaoBuscado);

        System.out.println("Esse ator já trabalhou nas seguintes séries:");
        seriesQueContemAtor.forEach(s -> System.out.println(s.getTitulo() + " - avaliação: " + s.getAvaliacao()));
    }

    private void vizualizarTop5(){
        System.out.println("Essas são as TOP5 séries mais bem avaliadas:");
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s -> System.out.println(s.getTitulo() + " - avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Digite abaixo a categoria/genêro que deseja buscar:");
        var opcaoCategoria = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(opcaoCategoria);
        List<Serie> seriePorCategoria = repositorio.findByGenero(categoria);

        seriePorCategoria.forEach(System.out::println);
    }

    public void buscarSerieComLimiteDeTemporada(){
        System.out.println("Com até quantas temporadas você deseja vizualizar?");
        var opcaoNumeroDeTemporadas = leitura.nextInt();
        System.out.println("A partir de qual nota você deseja?");
        var opcaoDeAvaliacao = leitura.nextDouble();

        List<Serie> seriesComLimiteDeTemporada = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(opcaoNumeroDeTemporadas, opcaoDeAvaliacao);
        seriesComLimiteDeTemporada.forEach(System.out::println);
    }
}