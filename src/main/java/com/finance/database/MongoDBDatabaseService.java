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
    public void deleteGelirKategorisi(String id) {

    }

    @Override
    public List<Gelir> getAllGelirler() {
        List<Gelir> gelirler = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("gelirler");
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Gelir gelir = new Gelir();
                gelir.setId(doc.getObjectId("_id").toHexString());
                gelir.setKategoriId(doc.getString("kategori_id"));
                gelir.setTutar(doc.getDouble("tutar"));
                gelir.setAciklama(doc.getString("aciklama"));
                if (doc.containsKey("tarih")) {
                    gelir.setTarih(doc.getDate("tarih").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                }
                if (doc.containsKey("olusturma_tarihi")) {
                    gelir.setOlusturmaTarihi(doc.getDate("olusturma_tarihi").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                }
                gelirler.add(gelir);
            }
        }
        return gelirler;
    }

    @Override
    public void addGelir(Gelir gelir) {
        MongoCollection<Document> collection = database.getCollection("gelirler");
        Document doc = new Document()
                .append("kategori_id", gelir.getKategoriId())
                .append("tutar", gelir.getTutar())
                .append("aciklama", gelir.getAciklama())
                .append("tarih", java.sql.Date.valueOf(gelir.getTarih()))
                .append("olusturma_tarihi", java.sql.Timestamp.valueOf(gelir.getOlusturmaTarihi()));
        collection.insertOne(doc);
    }

    @Override
    public void updateGelir(Gelir gelir) {
        MongoCollection<Document> collection = database.getCollection("gelirler");
        Document update = new Document()
                .append("kategori_id", gelir.getKategoriId())
                .append("tutar", gelir.getTutar())
                .append("aciklama", gelir.getAciklama())
                .append("tarih", java.sql.Date.valueOf(gelir.getTarih()))
                .append("olusturma_tarihi", java.sql.Timestamp.valueOf(gelir.getOlusturmaTarihi()));
        collection.updateOne(
                new Document("_id", new org.bson.types.ObjectId(gelir.getId())),
                new Document("$set", update)
        );
    }

    @Override
    public void deleteGelir(String id) {
        MongoCollection<Document> collection = database.getCollection("gelirler");
        collection.deleteOne(new Document("_id", new org.bson.types.ObjectId(id)));
    }

    @Override
    public List<GiderKategorisi> getAllGiderKategorileri() {
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
    public void updateGiderKategorisi(GiderKategorisi kategori) {}

    @Override
    public void deleteGiderKategorisi(String id) {}

    @Override
    public List<Gider> getAllGiderler() {
        return new ArrayList<>();
    }

    @Override
    public void addGider(Gider gider) {}

    @Override
    public void updateGider(Gider gider) {}

    @Override
    public void deleteGider(String id) {}

    @Override
    public List<AylikButce> getAllAylikButceler() {
        return new ArrayList<>();
    }

    @Override
    public void addAylikButce(AylikButce butce) {}

    @Override
    public void updateAylikButce(AylikButce butce) {}

    @Override
    public void deleteAylikButce(String id) {}

    @Override
    public List<TasarrufHedefi> getAllTasarrufHedefleri() {
        return new ArrayList<>();
    }

    @Override
    public void addTasarrufHedefi(TasarrufHedefi hedef) {}

    @Override
    public void updateTasarrufHedefi(TasarrufHedefi hedef) {}

    @Override
    public void deleteTasarrufHedefi(String id) {}

    @Override
    public List<Not> getAllNotlar() {
        return new ArrayList<>();
    }

    @Override
    public void addNot(Not not) {}

    @Override
    public void updateNot(Not not) {}

    @Override
    public void deleteNot(String id) {}

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