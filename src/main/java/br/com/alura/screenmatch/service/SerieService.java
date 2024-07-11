package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpsodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repositorio;

    public  List<SerieDTO> converteDados(List<Serie> series){
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterTodasAsSeries(){
        return converteDados(repositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series(){
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());
    }


    public List<SerieDTO> obterLancamentosSeries() {
        return converteDados(repositorio.encontrarEpsodiosMaisRecentes());
    }

    public SerieDTO obterSeriePorId(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
        }
        return null;
    }

    public List<EpsodioDTO> obterEpsodiosTodasTemporada(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return s.getEpsodios().stream()
                    .map(e -> new EpsodioDTO(e.getNumeroEpisodio(), e.getTemporada(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpsodioDTO> obterEpsodiosPorTemporada(Long id, Long numero) {
        return repositorio.buscarEpsodiosPorTemporada(id, numero)
                .stream()
                .map(e -> new EpsodioDTO(e.getNumeroEpisodio(), e.getTemporada(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterSeriesPorTemporada(String categoria) {
        Categoria genero = Categoria.fromPortugues(categoria);
        return converteDados(repositorio.findByGenero(genero));
    }

    public List<EpsodioDTO> obterTop5Epsodios(Long id) {
        List<Episodio> epsodios = repositorio.top5EpsodiosPorSerieID(id);
        return epsodios.stream()
                .map(e -> new EpsodioDTO(e.getNumeroEpisodio(), e.getTemporada(), e.getTitulo()))
                .collect(Collectors.toList());
    }
}
