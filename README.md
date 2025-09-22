# Auction Management System

A comprehensive Java-based auction management platform that enables secure bidding operations with role-based access control and transactional integrity.

## 📚 Table of Contents

- [Features](#-features)
  - [Core Functionality](#core-functionality)
  - [Key Highlights](#key-highlights)
- [Architecture](#️-architecture)
  - [Entity Relationships](#entity-relationships)
  - [Database Schema](#database-schema)
- [Technology Stack](#️-technology-stack)
- [Installation & Setup](#-installation--setup)
  - [Prerequisites](#prerequisites)
  - [Setup Instructions](#setup-instructions)
- [Available Operations](#-available-operations)
- [Security & Authorization](#-security--authorization)
  - [Testing Support](#testing-support)
  - [Role Permissions](#role-permissions)
- [Transaction Management](#-transaction-management)
- [Data Models](#-data-models)
  - [Core Entities](#core-entities)
- [Wrap-up](#wrap-up)

## 🚀 Features

### Core Functionality
- **Multi-role User Management**: Support for Admins, Bidders, and Initiators with distinct permissions
- **Auction House Management**: Create and manage auction institutions with configurable fees
- **Item Listing**: Add items for auction with detailed descriptions and ownership tracking
- **Real-time Bidding**: Secure bidding system with card-based payments
- **Transaction Safety**: Atomic operations with rollback capabilities to ensure data consistency

### Key Highlights
- **Fund Blocking**: When a bid is placed, funds are temporarily blocked on the bidder's card to ensure payment availability
- **Automated Settlement**: Upon auction completion, funds are automatically transferred from the winning bidder's account
- **Permission System**: Role-based access control prevents unauthorized operations
- **Audit Trail**: Complete transaction history and bid tracking for transparency

## 🏗️ Architecture

### Entity Relationships
- **Auctions**: Container for items with configurable transaction fees
- **Users**: Three role types (Admin, Bidder, Initiator) with different privileges
- **Items**: Products available for bidding, linked to auctions and initiators
- **Bids**: Individual bid records with user, item, and payment card associations
- **Cards**: Payment methods with balance tracking and fund blocking capabilities

### Database Schema
The system uses PostgreSQL with the following core tables:
- `auctions` - Auction house information
- `users` - User accounts with role definitions
- `cards` - Payment card details with balance management
- `items` - Auction items with status tracking
- `bids` - Bidding history and current bid tracking

## 🛠️ Technology Stack

- **Language**: Java (SDK v21, Language Level v18)
- **Database**: PostgreSQL v17.5
- **JDBC**: PostgreSQL JDBC Driver
- **Architecture**: MVC pattern with service layer abstraction

## 📦 Installation & Setup

### Prerequisites
1. Java Development Kit (JDK) 21 or higher
2. PostgreSQL 17.5 or compatible version
3. IntelliJ IDEA or compatible IDE

### Setup Instructions

1. **Database Setup**
   ```bash
   # Install PostgreSQL and create a local database
   createdb auction_management
   ```

2. **JDBC Driver Installation**
   - Download the PostgreSQL JDBC driver from [jdbc.postgresql.org](https://jdbc.postgresql.org/)
   - In IntelliJ: Go to `File > Project Structure > Libraries > +` and add the JDBC JAR file

3. **Database Configuration**
   - Create a `.env` file in the `/src` directory
   - Configure your database connection parameters:
     ```
     DB_URL=jdbc:postgresql://localhost:5432/auction_management
     DB_USER=your_username
     DB_PASSWORD=your_password
     ```

4. **Initialize Database Schema**
   - Execute the SQL commands from `src/database_commands.sql` in your PostgreSQL database

5. **Run the Application**
   - Compile and run the `Main.java` class
   - Follow the interactive menu to perform operations

## 🎮 Available Operations

| Operation | Description | Required Role |
|-----------|-------------|---------------|
| **User Management** |
| Create User | Register new users (Admin, Bidder, Initiator) | Any |
| Delete User | Remove user accounts | Admin |
| Show All Users | Display registered users | Any |
| **Card Management** |
| Show All Cards | Display user's payment cards | Owner/Admin |
| Add Card | Register new payment card | Owner |
| Add Funds | Add money to card balance | Owner |
| **Auction Management** |
| Create Auction | Establish new auction house | Admin |
| Show All Auctions | List available auction houses | Any |
| Delete Auction | Remove auction and refund blocked funds | Admin |
| **Item Management** |
| Add Item | List item for auction | Initiator |
| Show All Items | Display items in auction house | Any |
| **Bidding Operations** |
| Make Bid | Place bid on item | Bidder/Admin |
| Show Product Bids | View all bids for specific item | Admin/Owner |
| Show Made Bids | Display user's active bids | Owner/Admin |
| Finish Bidding | Complete auction and process payment | Admin |
| Cancel Bidding | Cancel auction and refund blocked funds | Admin |

## 🔐 Security & Authorization

**Important**: This application follows an API-like design pattern where operations require UUID-based entity identification for authorization and validation.

### Testing Support
- Use the provided `testing_data_support.txt` file to store and copy UUIDs for testing
- This approach ensures proper entity relationships and prevents unauthorized access

### Role Permissions
- **Admin**: Full system access, can perform all operations
- **Bidder**: Can place bids, manage own cards and view own data
- **Initiator**: Can add items for auction, manage own cards and view own data

## 🔄 Transaction Management

The system implements atomic transactions for complex operations:
- **Bid Placement**: Unblocks previous bidder's funds → Blocks new bidder's funds → Creates bid record
- **Auction Completion**: Transfers funds from winner → Updates item status → Records transaction
- **Rollback Support**: If any step fails, all previous operations are automatically reversed

## 📊 Data Models

### Core Entities
```java
// Auction: Auction house with fee structure
auction: { auctionId, name, fare, items[] }

// User: System participant with role-based permissions  
user: { userId, fullName, role, cards[] }

// Item: Auctionable product
item: { itemId, description, initiatorId, auctionId, active, bids[] }

// Bid: Individual bid record
bid: { bidId, itemId, userId, cardId, bidSum, timestamp }

// Card: Payment method with balance tracking
card: { cardId, userId, holderName, expirationMonth, expirationYear, balance, blockedSum }
```

## Wrap-up

This project demonstrates enterprise-level auction management concepts including:
- Transactional integrity in financial operations
- Role-based access control implementation
- Real-time fund blocking and settlement
- Comprehensive audit trail maintenance

---

*For development questions or issues, please refer to the source code documentation and database schema definitions.*
