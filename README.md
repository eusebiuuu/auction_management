### Descriere generala
Proiectul reprezinta o aplicatie de gestionare a licitatiilor ce are in componenta sa urmatoarele entitati: Useri (Admini, Bidderi si Initiators), Auctions, Items, Bid, Card. Design-ul aplicatiei presupune ca o licitatie are una sau mai multe produse disponibile (marcate ca active), iar Bidder-ii si Adminii sunt singurii care pot licita pentru produse. Atunci cand se realizeaza o licitare, se va crea un obiect de tipul Bid care determina licitatia, produsul, licitatorul si suma licitata.   
O functionalitate relevanta, este cea de licitatie: pe scurt atunci cand se liciteaza, se va bloca in cardul licitatorului suma licitata astfel incat sa nu poata fi scoasa respectiva suma de pe card si sa nu mai existe bani pentru produsul licitat in cazul in care acea persoana este chiar cea castigatoare. In momentul in care o licitatie se termina, se va extrage efectiv suma de pe card sub forma unei tranzactii. Am gandit asa, deoarece in practica tranzactiile bancare sunt lente, iar in cazul licitatiilor este de multe ori nevoie de viteza.   
O alta functionalitate importanta, este cea de gestionare a erorilor de autentificare (atunci cand un user de tipul Initiator doreste sa liciteze sau un user de tipul Bidder sa adauge produse), cazuri de validare de input sau cazuri in care nu exista valori (de ex.: atunci cand se termina licitatia, iar lista este goala).   
Operatii disponibile:
- Create user
- Delete user (userID) -> check if admin
- Show all users (userID) -> check if admin
- Show all cards (userID)
- Add card (userID)
- Update card balance
- Make bid (productID, auctionID, cardID, sum) -> check if bidder -> validations & checks & block sums & rollback the previous payment
- Create auction (userID) -> check if admin
- Show all auctions
- Show all items (auctionID) (by default they are active)
- Create item (auctionID, userID, fields) -> check if initiator & pay tax
- Finish the auction for a certain product (auctionID, productID) -> check if user is admin or initiator -> payment -> checks

### Entities
- auction: list of products, tax per product
- bid: userID, bidSum, card
- product: author, initiatorID, description, amount, list of bids, activeFlag
- user (admin, initiator, bidder): fullName, list of cards
- card: code, name, expiration month, expiration year, balance, blockedSum
- Menu (singleton): list of auctions ordered by tax, list of users

### Actions
- Create user
- Delete user
- Show all users
- Show all cards for a specific user
- Add card
- Update card balance
- Make bid: check if bidder, validations, checks, block sums, rollback the previous payment

- Create auction
- Show all auctions
- Show all items (auctionID) (by default they are active)
- Create item: check if initiator & pay tax
- Finish the auction for a certain product (auctionID, productID) -> check if user is admin or initiator -> payment -> checks
- Cancel auction for certain product (userID, productID) -> user is admin -> rollback the blocked sums
---

### Tasks
- Testing
- Refactor documentation

- Design patterns

Etapa II
5) Extindeți proiectul din prima etapă prin realizarea persistenței utilizând o bază de date relațională
   și JDBC.
   Să se realizeze servicii care sa expună operații de tip create, read, update și delete pentru cel puțin 4
   dintre clasele definite. Se vor realiza servicii singleton generice pentru scrierea și citirea din baza de
   date.
6) Realizarea unui serviciu de audit
   Se va realiza un serviciu care să scrie într-un fișier de tip CSV de fiecare dată când este executată una
   dintre acțiunile descrise în prima etapă. Structura fișierului: nume_actiune, timestamp

Integrate the rest of the **Project Plan**