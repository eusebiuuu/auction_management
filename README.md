## Sistem de gestiune a licitatiilor

**IMPORTANT: Aplicatia a fost gandita sa semene mai mult cu un API i.e. la fiecare operatie trebuie introduse cateva id-uri de tip UUID pentru a asigura autorizarea si existenta entitatilor. Pentru usurarea testarii, recomand salvarea id-urilor intr-un fisier separat si copierea de acolo in consola (eu am folosit [testing_data_support.txt](testing_data_support.txt)**).

### 1. Descriere generala
- Proiectul reprezinta o aplicatie de gestionare a licitatiilor ce are in componenta sa urmatoarele entitati: Useri (Admini, Bidderi si Initiators), Auctions, Items, Bid, Card. Design-ul aplicatiei presupune ca o licitatie are una sau mai multe produse disponibile (marcate ca active), iar Bidder-ii si Admin-ii sunt singurii care pot licita pentru produse. Atunci cand se realizeaza o licitare, se va crea un obiect de tipul Bid care determina licitatia, produsul, licitatorul si suma licitata.   
- O functionalitate relevanta, este cea de licitatie: pe scurt atunci cand se liciteaza, se va bloca in cardul licitatorului suma licitata astfel incat sa nu poata fi scoasa respectiva suma de pe card si sa nu mai existe bani pentru produsul licitat in cazul in care acea persoana este chiar cea castigatoare. In momentul in care o licitatie se termina, se va extrage efectiv suma de pe card sub forma unei tranzactii. Am gandit asa, deoarece in practica tranzactiile bancare sunt lente, iar in cazul licitatiilor este de multe ori nevoie de viteza.   
- O alta functionalitate importanta, este cea de gestionare a erorilor de autentificare (atunci cand un user de tipul Initiator doreste sa liciteze sau un user de tipul Bidder sa adauge produse), cazuri de validare de input sau cazuri in care nu exista valori (de ex.: atunci cand se termina licitatia, iar lista este goala).  
- O ultima functionalitate semnificativa reprezinta atomizarea tranzactiilor. Pe scurt, atunci cand utilizatorul foloseste anumite functii ce au in componenta mai multe etape (de ex., pentru functia de licitare, trebuie deblocata suma blocata pentru ultimul licitator, apoi blocata suma introdusa de utilizatorul curent, iar la final de creat o inregistrare in tabelul `bids` corespunzatoare bid-ului realizat de ultimul user) astfel incat, daca una din ele esueaza, sa se realizeze un `undo` pentru operatiile realizate pana atunci.

### 2. Tehnologii folosite
- Proiectul a fost implementat in Java cu SDK v21 si Language Level v18 (informatii luate din `File > Project Structure`).
- Pentru baza de date am folosit PostgreSQL v17.5.

### 3. Installation setup
Pentru a putea rula aplicatia local, va trebui sa realizati urmatorii pasi:
- Instalati pachetul de postgresql jdbc de [aici](https://jdbc.postgresql.org/)
- Adaugati-l in proiect mergand la `File > Project Structure > Libraries > +` si adaugand fisierul respectiv in Intellj.
- Instalati si configurati o baza de date postgresql local
- Schimbati valorile parametrilor de conexiune `DB_URL`, `DB_PASSWORD`, `DB_USER` din fisierul `/src/.env` cu parametrii bazei dumneavoastra de date (din moment ce baza de date este locala imi permit sa pun fisierul pe github).

### 4. Operatii disponibile
- Create user: Creati un user din cele 3 tipuri disponibile. **Atentie!** User-ii au diferite permisiuni. Doar admin-ul are permisiuni absolute.
- Delete user (userID): Se sterge un user.
- Show all users (userID): Se afiseaza toti user-ii.
- Show all cards (userID): Se afiseaza toate cardurile unui anumit user.
- Add card (userID): Se adauga un card user-ului introdus.
- Create auction: Se creeaza o institutie ce se ocupa cu licitatii ce are o anumita taxa.
- Show all auctions: Se afiseaza toate institutiile de licitatii
- Show all items: Se afiseaza toate item-ele pe care se poate licita la o anumita institutie
- Add item: Se creeaza un item pentru care se va putea licita
- Finish bidding: Se va termina licitatia pentru un anumit item. Asta inseamna ca ultimului user i se va scadea din cont suma licitata.
- Make bid: User-ul introdus va putea licita la o anumita institutie, pentru un anumit produs, cu un anumit card.
- Add sum to card: Se va adauga o suma de bani pe cardul introdus
- Show all product bids: Se vor afisa toate bid-urile pentru un anumit produs. Permis doar admin-ilor si owner-ului.
- Show all made bids: Se vor afisa toate bid-urile realizate pentru licitatii active.
- Cancel bidding: Se va anula licitatia pentru produsul introdus (niciun bidder nu va pierde bani)
- Delete auction: Se va sterge institutia de licitare introdusa impreuna cu toate licitatiile si bid-urile aferente. Sumele blocate user-ilor vor fi restituite.

### 5. Entitatile
- auction: list of items, tax per product, name
- bid: userID, bidSum, cardID, itemID
- item: author, initiatorID, cardID, description, amount, list of bids
- user (admin, initiator, bidder): fullName, list of cards
- card: code, name, expiration month, expiration year, balance, blockedSum
- Menu (singleton): list of auctions ordered by tax, list of users
