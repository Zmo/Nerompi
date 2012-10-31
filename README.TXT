===========================================
 Nero työhuoneiden hallinnointijärjestelmä
===========================================

0. CD:n sisältö
----------------

README.TXT      Lienee tämä tiedosto
LICENSE.TXT     Nero-ohjelmiston lisenssitiedosto (LGPL)
doc/            Ohjelmistoon liittyvät dokumentit
nero-1.0/       Suoritettava ohjelma
src/            Ohjelmiston lähdekoodit
files/          Sekä Neron että käytettyjen kirjastojen asennuspaketit

1. Asennus
-----------

Käyttääksesi ohjelmaa tarvitset Sun Java 1.4.2 tai uudemman.

Helpoin tapa asentaa Nero on kopioida nero-1.0 -hakemisto, joka sisältää
kaikki tarvittavat tiedostot ja kirjastot.

Nero voidaan asentaa myös purkamalla files-hakemistosta joko nero-1.0.tar.gz
tai nero-1.0.zip paketti haluttuun hakemistoon. Tämän lisäksi ohjelmisto
tarvitsee ulkoisia kirjastoja toimiakseen. Näitä kirjastoja ei ole pakattu
mukaan Neron paketteihin. Näiden kirjastojen JAR-tiedostot tulee purkaa
Neron lib-alihakemistoon. Tarvittavia kirjastoja ovat Batik (batik-1.5.1.zip),
Oracle JDBC -ajuri (ojdbc14.jar) sekä JGoodies Looks 1.2.2 (looks-1_2_2.zip).

Tämän jälkeen editoidaan nero.ini -tiedostoon tietokannan nimi, käyttäjätunnus
ja salasana.

Ohjelma on näin käyttövalmis.


2. Käynnistys
--------------

Ohjelma käynnistetään Linuxissa nero.sh -komennolla ja Windowsissa nero.cmd -
komennolla. Ohjelmalle voidaan antaa käynnistysparametrina vaihtoehtoinen
ini-tiedosto, joka mahdollistaa ohjelman suorittamisen myös suoraan cd:ltä,
kunhan on ensin kopioinut cd:ltä ini-tiedoston ja muokannut sen
tietokanta-asetukset kuntoon.


3. Ohjelman tekijät
--------------------

Johannes Kuusela
Jyrki Muukkonen
Teemu Sjöblom
Ville Sundberg
Osma Suominen
Timi Tuohenmaa

