package br.ufg.inf.fabrica.persistencia.sqlite;

import br.ufg.inf.fabrica.persistencia.DataValueRepository;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.openehr.rm.datatypes.uri.DvEHRURI;

import java.util.UUID;

/**
 * Testes com DvURI.
 */
public class DvEHRURIRepositoryTest extends TestCase {

    private DataValueRepository repo;

    @Override
    public void setUp() {
        // Obtém repositório

        repo = new DataValueRepositorySqlite();
    }

    public void testPersisteRecupera() {
        DvEHRURI ehruri = new DvEHRURI("https://github.com");

        String key = UUID.randomUUID().toString();

        repo.save(key, ehruri);

        Assert.assertEquals(ehruri, repo.get(key));
    }
}
