package com.finance.database;


import com.finance.model.*;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoDBDatabaseService implements DatabaseService {
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoDBDatabaseService() {
        this.mongoClient = DatabaseManager.getInstance().getMongoClient();
        this.database = mongoClient.getDatabase("finance_manager");
    }

    private Document convertToDocument(GelirKategorisi kategori) {
        return new Document()
                .append("ad", kategori.getAd())
                .append("olusturma_tarihi", Date.from(kategori.getOlusturmaTarihi()
                        .atZone(ZoneId.systemDefault()).toInstant()));
    }

    private GelirKategorisi convertToGelirKategorisi(Document doc) {
        GelirKategorisi kategori = new GelirKategorisi();
        kategori.setId(doc.get("_id").toString());
        kategori.setAd(doc.getString("ad"));
        kategori.setOlusturmaTarihi(doc.getDate("olusturma_tarihi")
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return kategori;
    }

    @Override
    public List<GelirKategorisi> getAllGelirKategorileri() {
        List<GelirKategorisi> kategoriler = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("gelir_kategorileri");
        
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                kategoriler.add(convertToGelirKategorisi(cursor.next()));
            }
        }
        return kategoriler;
    }

    @Override
    public void addGelirKategorisi(GelirKategorisi kategori) {
        MongoCollection<Document> collection = database.getCollection("gelir_kategorileri");
        if (collection.countDocuments(new Document("ad", kategori.getAd())) > 0) {
            throw new RuntimeException("Bu isimde bir gelir kategorisi zaten var!");
        }
        Document doc = convertToDocument(kategori);
        collection.insertOne(doc);
    }

    @Override
    public void updateGelirKategorisi(GelirKategorisi kategori) {
        MongoCollection<Document> collection = database.getCollection("gelir_kategorileri");
        collection.updateOne(
            Filters.eq("_id", kategori.getId()),
            Updates.set("ad", kategori.getAd())
        );
    }

    @Override
    public void deleteGelirKategorisi(int id) {
        MongoCollection<Document> collection = database.getCollection("gelir_kategorileri");
        collection.deleteOne(Filters.eq("_id", id));
    }

    // Implement other methods similarly...
    // For brevity, I'm showing just the GelirKategorisi methods as an example
    // The other methods would follow the same pattern

    @Override
    public List<Gelir> getAllGelirler() {
        // TODO: Implement
        return new ArrayList<>();
    }

    @Override
    public void addGelir(Gelir gelir) {
        // TODO: Implement
    }

    @Override
    public void updateGelir(Gelir gelir) {
        // TODO: Implement
    }

    @Override
    public void deleteGelir(int id) {
        // TODO: Implement
    }

    @Override
    public List<GiderKategorisi> getAllGiderKategorileri() {
        // TODO: Implement
        return new ArrayList<>();
    }

    private Document convertToDocument(GiderKategorisi kategori) {
        return new Document()
                .append("ad", kategori.getAd())
                .append("olusturma_tarihi", Date.from(kategori.getOlusturmaTarihi().atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public void addGiderKategorisi(GiderKategorisi kategori) {
        MongoCollection<Document> collection = database.getCollection("gider_kategorileri");
        if (collection.countDocuments(new Document("ad", kategori.getAd())) > 0) {
            throw new RuntimeException("Bu isimde bir gider kategorisi zaten var!");
        }
        Document doc = convertToDocument(kategori);
        collection.insertOne(doc);
    }

    @Override
    public void updateGiderKategorisi(GiderKategorisi kategori) {
        // TODO: Implement
    }

    @Override
    public void deleteGiderKategorisi(int id) {
        // TODO: Implement
    }

    @Override
    public List<Gider> getAllGiderler() {
        // TODO: Implement
        return new ArrayList<>();
    }

    @Override
    public void addGider(Gider gider) {
        // TODO: Implement
    }

    @Override
    public void updateGider(Gider gider) {
        // TODO: Implement
    }

    @Override
    public void deleteGider(int id) {
        // TODO: Implement
    }

    @Override
    public List<AylikButce> getAllAylikButceler() {
        // TODO: Implement
        return new ArrayList<>();
    }

    @Override
    public void addAylikButce(AylikButce butce) {
        // TODO: Implement
    }

    @Override
    public void updateAylikButce(AylikButce butce) {
        // TODO: Implement
    }

    @Override
    public void deleteAylikButce(int id) {
        // TODO: Implement
    }

    @Override
    public List<TasarrufHedefi> getAllTasarrufHedefleri() {
        // TODO: Implement
        return new ArrayList<>();
    }

    @Override
    public void addTasarrufHedefi(TasarrufHedefi hedef) {
        // TODO: Implement
    }

    @Override
    public void updateTasarrufHedefi(TasarrufHedefi hedef) {
        // TODO: Implement
    }

    @Override
    public void deleteTasarrufHedefi(int id) {
        // TODO: Implement
    }

    @Override
    public List<Not> getAllNotlar() {
        // TODO: Implement
        return new ArrayList<>();
    }

    @Override
    public void addNot(Not not) {
        // TODO: Implement
    }

    @Override
    public void updateNot(Not not) {
        // TODO: Implement
    }

    @Override
    public void deleteNot(int id) {
        // TODO: Implement
    }

    @Override
    public void addRapor(int ay, int yil, double gelir, double gider, double bakiye) {
        // MongoDB için rapor kaydetme desteği henüz eklenmedi.
    }

    @Override
    public java.util.List<com.finance.model.Rapor> getAllRaporlar() {
        // MongoDB için rapor geçmişi desteği henüz eklenmedi.
        return new java.util.ArrayList<>();
    }
} 