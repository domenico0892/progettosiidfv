# Crawler per SII
## Francesco Di Cara, Domenico Giammarino, Vincenzo Giordano

it.uniroma3.model:
- Controller -> avvia il crawler con le relative configurazioni
- MyCrawler -> contiene 'shouldVisit' per decidere se visitare la pagina e 'visit' per le operazioni sulla singola pagina
- SingleResult -> modella la pagina visitata e la trasforma in Document per MongoDB

it.uniroma3.persistance:
- MongoConnection -> richiede la connessione a MongoDB

