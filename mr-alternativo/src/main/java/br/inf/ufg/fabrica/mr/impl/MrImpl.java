/*
 * Copyright (c) 2015 - 2016. Instituto de Informática (UFG)
 */

package br.inf.ufg.fabrica.mr.impl;

import br.inf.ufg.fabrica.mr.Mr;
import br.inf.ufg.fabrica.mr.datatypes.Text;
import br.inf.ufg.fabrica.mr.mrbuffers.MrBufferBuilder;
import io.netty.buffer.ByteBuf;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * Implementação do modelo de referência.
 */
public class MrImpl implements Mr {

    MrBufferBuilder bufferBuilder;
    MrBufferBuilder vectorBB;

    public MrImpl(int initial_size) {
        if (initial_size <= 0) initial_size = 1;
        bufferBuilder = new MrBufferBuilder(initial_size);
        // header
        bufferBuilder.addInt(0); // list index
        vectorBB = new MrBufferBuilder(initial_size);
    }

    public MrImpl() {
        this(1);
    }

    public int getListIndex() {
        return 0;
    }

    public MrBufferBuilder getBufferBuilder() {
        return bufferBuilder;
    }

    public MrBufferBuilder getVectorBB() {
        return vectorBB;
    }

    /**
     * Adiciona um valor lógico
     * - {@code DV_BOOLEAN}.
     *
     * @param valor Valor lógico (DV_BOOLEAN) a ser adicionado.
     * @return Identificador do valor lógico adicionado.
     */
    public int adicionaDvBoolean(boolean valor) {
        int id = bufferBuilder.addType(DV_BOOLEAN);
        bufferBuilder.addBoolean(valor);
        return id;
    }

    /**
     * Adiciona um identificador
     * - {@code DV_IDENTIFIER}.
     *
     * @param issuer   Entidade que emite identificação.
     * @param assigner Entidade que assina identificação.
     * @param id       Identificador propriamente dito.
     * @param type     Tipo da identificação.
     * @return O identificador único deste identificador na estrutura.
     */
    public int adicionaDvIdentifier(String issuer, String assigner, String id, String type) {
        int i = bufferBuilder.addType(DV_IDENTIFIER);
        bufferBuilder.addInt(vectorBB.createString(issuer));
        bufferBuilder.addInt(vectorBB.createString(assigner));
        bufferBuilder.addInt(vectorBB.createString(id));
        bufferBuilder.addInt(vectorBB.createString(type));
        return i;
    }

    /**
     * Adiciona um valor de estado
     * - {@code DV_STATE}.
     *
     * @param value    {@code DV_CODED_TEXT}
     * @param terminal
     * @return
     * @see Text#adicionaDvCodedText(String, String, String, int, String, String, int)
     */
    public int adicionaDvState(int value, boolean terminal) {
        int id = bufferBuilder.addType(DV_STATE);
        bufferBuilder.addInt(value);
        bufferBuilder.addBoolean(terminal);
        return id;
    }

    /**
     * Adiciona um {@link URI} ({@code DV_URI}).
     *
     * @param uri Sequência de caracteres correspondentes
     *            à {@link URI}.
     * @return O identificador único desta URI na estrutura.
     */
    public int adicionaDvUri(String uri) {
        int id = bufferBuilder.addType(DV_URI);
        bufferBuilder.addInt(vectorBB.createString(uri));
        return id;
    }

    /**
     * Adiciona um {@link URI} cujo esquema é
     * "ehr" ({@code DvEHRURI}).
     *
     * @param uri Sequência de caracteres correspondentes
     *            à {@link URI}.
     * @return O identificador único desta DvEHRURI na estrutura.
     */
    public int adicionaDvEhrUri(String uri) {
        int id = bufferBuilder.addType(DV_EHR_URI);
        bufferBuilder.addInt(vectorBB.createString(uri));
        return id;
    }

    /**
     * Adiciona uma série de textos simples para ser exibido como um parágrafo
     * - {@code DV_PARAGRAPH }.
     *
     * @param itemsList A sequência de {@code DV_TEXT}s.
     * @return O identificador único do parágrafo na estrutura.
     */
    public int adicionaDvParagraph(int itemsList) {
        return 0;
    }

    /**
     * Adiciona uma expressão de texto com qualquer quantidade de caracteres podendo formar palavras,
     * sentenças e etc. Formatação visual e hiperlinks podem ser incluídos
     * - {@code DV_TEXT}.
     *
     * @param value
     * @param hyperlink          {@code DV_URI}
     * @param formatting
     * @param mappings           {@code TERM_MAPPING}
     * @param codePhraseLanguage {@code CODE_PHRASE}
     * @param codePhraseEncoding {@code CODE_PHRASE}
     * @return identificador único da exoressão de texto na extrutura.
     */
    public int adicionaDvText(String value, String hyperlink, String formatting, int mappings, String codePhraseLanguage, String codePhraseEncoding) {
        int hyperlinkId = adicionaDvUri(hyperlink);
        int laguageId = adicionaCodePhrase(codePhraseLanguage);
        int encodingId = adicionaCodePhrase(codePhraseEncoding);
        int id = bufferBuilder.addType(DV_TEXT);
        bufferBuilder.addInt(mappings);
        bufferBuilder.addInt(hyperlinkId);
        bufferBuilder.addInt(laguageId);
        bufferBuilder.addInt(encodingId);
        bufferBuilder.addInt(vectorBB.createString(formatting));
        bufferBuilder.addInt(vectorBB.createString(value));
        return id;
    }

    /**
     * Adiciona um código
     * - {@code CODE_PHRASE}.
     *
     * @param value
     * @return O identificador único do código na estrutura.
     */
    public int adicionaCodePhrase(String value) {
        int id = bufferBuilder.addType(CODE_PHRASE);
        bufferBuilder.addInt(vectorBB.createString(value));
        return id;
    }

    /**
     * @param value
     * @param hyperlink          {@code DV_URI}
     * @param formatting
     * @param mappings           {@code TERM_MAPPING}
     * @param codePhraseLanguage {@code CODE_PHRASE}
     * @param codePhraseEncoding {@code CODE_PHRASE}
     * @param definingCode
     * @return identificador único da exoressão de texto na extrutura.
     * @see #adicionaDvText(String, String, String, int, String, String)
     * @see #adicionaCodePhrase(String, String)
     */
    public int adicionaDvCodedText(String value, String hyperlink, String formatting, int mappings, String codePhraseLanguage, String codePhraseEncoding, int definingCode) {
        return 0;
    }

    /**
     * TODO: como tratar referencias e valores nulos? - ocupar o espaço nulo com bytes vazios
     *
     * @param target  O termo alvo do mapeamento {@code CODE_PHRASE}.
     * @param match   Operador de equivalencia entre os termos.
     * @param purpose Finalidade do mapemento {@code DV_CODED_TEXT}. Ex: "automated", "data mining", "interoperability".
     * @return
     * @see #adicionaCodePhrase(String, String)
     * @see #adicionaDvCodedText(String, String, String, int, String, String, int)
     */
    public int adicionaTermMapping(int target, char match, int purpose) {
        int id = addIdFromType(TERMINOLOGY_ID, true);
        bufferBuilder.addInt(target);
        bufferBuilder.addInt(purpose);
        bufferBuilder.addChar(match);
        return id;
    }

    /**
     * Adiciona um identificador de terminologia ({@code TERMINOLOGY_ID}).
     *
     * @param valor Identificador de terminologia.
     * @return O identificador único do objeto criado.
     */
    public int adicionaTerminologyId(String valor) {
        return adicionaTerminologyId(valor, true);
    }

    public int adicionaTerminologyId(String valor, boolean withId) {
        int id = addIdFromType(TERMINOLOGY_ID, withId);
        bufferBuilder.addInt(vectorBB.createString(valor));
        return id;
    }

    private int addIdFromType(int type, boolean withId) {
        return withId ? bufferBuilder.addType(type) : bufferBuilder.offset();
    }

    public byte[] toByteArray() {
        return toByteByffer().array();
    }

    /**
     * @see #getListIndex()
     * @return
     */
    public ByteBuffer toByteByffer() {
        ByteBuf byteBuf = bufferBuilder.dataBuffer().copy();
        byteBuf.setInt(getListIndex(), byteBuf.writerIndex());
        byteBuf.writeBytes(vectorBB.dataBuffer().duplicate());
        return byteBuf.capacity(byteBuf.nioBuffer().remaining()).nioBuffer();
    }

    public static void printByteArray(byte[] buffer) {
        int i = 0;
        for (byte x : buffer) {
            System.out.println("" + i + " - " + x);
            i++;
        }
    }
}
