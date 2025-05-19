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

    @Override
    public List<Gelir> getAllGelirler() {
        List<Gelir> gelirler = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("gelirler");
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Gelir gelir = new Gelir();
                gelir.setId(Integer.parseInt(doc.get("_id").toString()));
                gelir.setKategoriId(doc.getString("kategori_id"));
                gelir.setTutar(doc.getDouble("tutar"));
                gelir.setAciklama(doc.getString("aciklama"));
                gelir.setTarih(doc.getDate("tarih").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                gelir.setOlusturmaTarihi(doc.getDate("olusturma_tarihi").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
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
                .append("tarih", Date.from(gelir.getTarih().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("olusturma_tarihi", Date.from(gelir.getOlusturmaTarihi().atZone(ZoneId.systemDefault()).toInstant()));
        collection.insertOne(doc);
    }

    @Override
    public void updateGelir(Gelir gelir) {
        MongoCollection<Document> collection = database.getCollection("gelirler");
        collection.updateOne(
            Filters.eq("_id", gelir.getId()),
            Updates.combine(
                Updates.set("kategori_id", gelir.getKategoriId()),
                Updates.set("tutar", gelir.getTutar()),
                Updates.set("aciklama", gelir.getAciklama()),
                Updates.set("tarih", Date.from(gelir.getTarih().atStartOfDay(ZoneId.systemDefault()).toInstant()))
            )
        );
    }

    @Override
    public void deleteGelir(int id) {
        MongoCollection<Document> collection = database.getCollection("gelirler");
        collection.deleteOne(Filters.eq("_id", id));
    }

    @Override
    public List<GiderKategorisi> getAllGiderKategorileri() {
        List<GiderKategorisi> kategoriler = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("gider_kategorileri");
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                GiderKategorisi kategori = new GiderKategorisi();
                kategori.setId(doc.get("_id").toString());
                kategori.setAd(doc.getString("ad"));
                kategori.setOlusturmaTarihi(doc.getDate("olusturma_tarihi").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                kategoriler.add(kategori);
            }
        }
        return kategoriler;
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
        MongoCollection<Document> collection = database.getCollection("gider_kategorileri");
        collection.updateOne(
            Filters.eq("_id", kategori.getId()),
            Updates.set("ad", kategori.getAd())
        );
    }

    @Override
    public void deleteGiderKategorisi(int id) {
        MongoCollection<Document> collection = database.getCollection("gider_kategorileri");
        collection.deleteOne(Filters.eq("_id", String.valueOf(id)));
    }

    @Override
    public List<Gider> getAllGiderler() {
        List<Gider> giderler = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("giderler");
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Gider gider = new Gider();
                gider.setId(Integer.parseInt(doc.get("_id").toString()));
                gider.setKategoriId(doc.getString("kategori_id"));
                gider.setTutar(doc.getDouble("tutar"));
                gider.setAciklama(doc.getString("aciklama"));
                gider.setTarih(doc.getDate("tarih").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                gider.setOlusturmaTarihi(doc.getDate("olusturma_tarihi").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                giderler.add(gider);
            }
        }
        return giderler;
    }

    @Override
    public void addGider(Gider gider) {
        MongoCollection<Document> collection = database.getCollection("giderler");
        Document doc = new Document()
                .append("kategori_id", gider.getKategoriId())
                .append("tutar", gider.getTutar())
                .append("aciklama", gider.getAciklama())
                .append("tarih", Date.from(gider.getTarih().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("olusturma_tarihi", Date.from(gider.getOlusturmaTarihi().atZone(ZoneId.systemDefault()).toInstant()));
        collection.insertOne(doc);
    }

    @Override
    public void updateGider(Gider gider) {
        MongoCollection<Document> collection = database.getCollection("giderler");
        collection.updateOne(
            Filters.eq("_id", gider.getId()),
            Updates.combine(
                Updates.set("kategori_id", gider.getKategoriId()),
                Updates.set("tutar", gider.getTutar()),
                Updates.set("aciklama", gider.getAciklama()),
                Updates.set("tarih", Date.from(gider.getTarih().atStartOfDay(ZoneId.systemDefault()).toInstant()))
            )
        );
    }

    @Override
    public void deleteGider(int id) {
        MongoCollection<Document> collection = database.getCollection("giderler");
        collection.deleteOne(Filters.eq("_id", id));
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