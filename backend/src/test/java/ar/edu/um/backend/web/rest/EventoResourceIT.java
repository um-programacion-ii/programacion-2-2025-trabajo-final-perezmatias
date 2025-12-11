package ar.edu.um.backend.web.rest;

import static ar.edu.um.backend.domain.EventoAsserts.*;
import static ar.edu.um.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.edu.um.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.backend.IntegrationTest;
import ar.edu.um.backend.domain.Evento;
import ar.edu.um.backend.repository.EventoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EventoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventoResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_HORA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_HORA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_UBICACION = "AAAAAAAAAA";
    private static final String UPDATED_UBICACION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRECIO = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/eventos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventoMockMvc;

    private Evento evento;

    private Evento insertedEvento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evento createEntity() {
        return new Evento()
            .titulo(DEFAULT_TITULO)
            .descripcion(DEFAULT_DESCRIPCION)
            .fechaHora(DEFAULT_FECHA_HORA)
            .ubicacion(DEFAULT_UBICACION)
            .precio(DEFAULT_PRECIO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evento createUpdatedEntity() {
        return new Evento()
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .fechaHora(UPDATED_FECHA_HORA)
            .ubicacion(UPDATED_UBICACION)
            .precio(UPDATED_PRECIO);
    }

    @BeforeEach
    void initTest() {
        evento = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEvento != null) {
            eventoRepository.delete(insertedEvento);
            insertedEvento = null;
        }
    }

    @Test
    @Transactional
    void createEvento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Evento
        var returnedEvento = om.readValue(
            restEventoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evento)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Evento.class
        );

        // Validate the Evento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEventoUpdatableFieldsEquals(returnedEvento, getPersistedEvento(returnedEvento));

        insertedEvento = returnedEvento;
    }

    @Test
    @Transactional
    void createEventoWithExistingId() throws Exception {
        // Create the Evento with an existing ID
        evento.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evento)))
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTituloIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evento.setTitulo(null);

        // Create the Evento, which fails.

        restEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evento)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaHoraIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evento.setFechaHora(null);

        // Create the Evento, which fails.

        restEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evento)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrecioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evento.setPrecio(null);

        // Create the Evento, which fails.

        restEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evento)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEventos() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList
        restEventoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evento.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].fechaHora").value(hasItem(DEFAULT_FECHA_HORA.toString())))
            .andExpect(jsonPath("$.[*].ubicacion").value(hasItem(DEFAULT_UBICACION)))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(sameNumber(DEFAULT_PRECIO))));
    }

    @Test
    @Transactional
    void getEvento() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get the evento
        restEventoMockMvc
            .perform(get(ENTITY_API_URL_ID, evento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(evento.getId().intValue()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.fechaHora").value(DEFAULT_FECHA_HORA.toString()))
            .andExpect(jsonPath("$.ubicacion").value(DEFAULT_UBICACION))
            .andExpect(jsonPath("$.precio").value(sameNumber(DEFAULT_PRECIO)));
    }

    @Test
    @Transactional
    void getNonExistingEvento() throws Exception {
        // Get the evento
        restEventoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEvento() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evento
        Evento updatedEvento = eventoRepository.findById(evento.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEvento are not directly saved in db
        em.detach(updatedEvento);
        updatedEvento
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .fechaHora(UPDATED_FECHA_HORA)
            .ubicacion(UPDATED_UBICACION)
            .precio(UPDATED_PRECIO);

        restEventoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEvento.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEvento))
            )
            .andExpect(status().isOk());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventoToMatchAllProperties(updatedEvento);
    }

    @Test
    @Transactional
    void putNonExistingEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(put(ENTITY_API_URL_ID, evento.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evento)))
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(evento))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evento)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventoWithPatch() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evento using partial update
        Evento partialUpdatedEvento = new Evento();
        partialUpdatedEvento.setId(evento.getId());

        partialUpdatedEvento.descripcion(UPDATED_DESCRIPCION).fechaHora(UPDATED_FECHA_HORA).ubicacion(UPDATED_UBICACION);

        restEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvento))
            )
            .andExpect(status().isOk());

        // Validate the Evento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEvento, evento), getPersistedEvento(evento));
    }

    @Test
    @Transactional
    void fullUpdateEventoWithPatch() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evento using partial update
        Evento partialUpdatedEvento = new Evento();
        partialUpdatedEvento.setId(evento.getId());

        partialUpdatedEvento
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .fechaHora(UPDATED_FECHA_HORA)
            .ubicacion(UPDATED_UBICACION)
            .precio(UPDATED_PRECIO);

        restEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvento))
            )
            .andExpect(status().isOk());

        // Validate the Evento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventoUpdatableFieldsEquals(partialUpdatedEvento, getPersistedEvento(partialUpdatedEvento));
    }

    @Test
    @Transactional
    void patchNonExistingEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, evento.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(evento))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(evento))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(evento)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEvento() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the evento
        restEventoMockMvc
            .perform(delete(ENTITY_API_URL_ID, evento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Evento getPersistedEvento(Evento evento) {
        return eventoRepository.findById(evento.getId()).orElseThrow();
    }

    protected void assertPersistedEventoToMatchAllProperties(Evento expectedEvento) {
        assertEventoAllPropertiesEquals(expectedEvento, getPersistedEvento(expectedEvento));
    }

    protected void assertPersistedEventoToMatchUpdatableProperties(Evento expectedEvento) {
        assertEventoAllUpdatablePropertiesEquals(expectedEvento, getPersistedEvento(expectedEvento));
    }
}
