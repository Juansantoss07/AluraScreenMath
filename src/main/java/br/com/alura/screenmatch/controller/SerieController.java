package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpsodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService service;

    @GetMapping
    public List<SerieDTO> obterSeries() {
        return service.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTop5() {
        return service.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos(){
        return service.obterLancamentosSeries();
    }

    @GetMapping("/{id}")
    public SerieDTO obterSerieDetalhada(@PathVariable Long id){
        return service.obterSeriePorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpsodioDTO> obterEpsodiosTodasTemporadas(@PathVariable Long id){
        return service.obterEpsodiosTodasTemporada(id);
    }

    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpsodioDTO> obterEpsodiosPorTemporada(@PathVariable Long id, @PathVariable Long numero){
        return service.obterEpsodiosPorTemporada(id, numero);
    }

    @GetMapping("/categoria/{categoria}")
    public List<SerieDTO> obterSeriesPorCategoria(@PathVariable String categoria){
        return service.obterSeriesPorTemporada(categoria);
    }

    @GetMapping("/{id}/temporadas/top5ep")
    public List<EpsodioDTO> obterTop5Epsodios(@PathVariable Long id){
        return service.obterTop5Epsodios(id);
    }
}