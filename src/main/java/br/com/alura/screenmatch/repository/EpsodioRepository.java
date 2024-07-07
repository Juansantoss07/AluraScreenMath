package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpsodioRepository extends JpaRepository<Episodio, Long> {
}
