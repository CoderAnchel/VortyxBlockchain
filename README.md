# VortyxBlockchain

Welcome to **VortyxBlockchain**, a Java-based blockchain implementation designed as a learning project to understand the core concepts of blockchain technology. This repository provides a hands-on approach to creating a simple yet functional blockchain, complete with features like transaction signing, Proof of Work (PoW), Merkle Trees, and more.

---

## 🚀 Introduction

Blockchain technology has revolutionized industries by offering decentralized, immutable, and transparent systems. This project aims to demystify blockchain by building one from scratch using **Java** and **Go**. Whether you're a seasoned Java developer or someone looking for a challenging coding project, this repository is for you.

By the end of this project, you’ll have:
- A local blockchain capable of signing transactions, mining blocks, and validating chains.
- A foundation for scaling to a fully decentralized network.

Read the full article on Medium for an in-depth explanation of the project:  

👉 [Creating a Blockchain with Java and Go Part 1: The Java Node](https://medium.com/@coderanchel/creating-a-blockchain-with-java-and-go-part-1-the-java-node-c10f5f51b60e)

## 🛠️ Features

### 🔐 Cryptographic Signatures
- Secure transactions using asymmetric cryptography.
- Transactions are signed with private keys and verified with public keys.
- Utilizes **ECDSA** via **Bouncy Castle** in Java for cryptographic operations.

### 🔨 Proof of Work (PoW)
- Prevents tampering by requiring miners to solve a computational puzzle.
- Blocks are mined by finding a valid nonce that produces a hash with a specific number of leading zeroes.

### 🌲 Merkle Tree
- Efficiently validates transactions in a block.
- Uses recursive hashing to generate a Merkle Root.

### 🕒 Mempool
- Holds unconfirmed transactions until they are included in a block.
- Ensures double-spending prevention and transaction validation.

### ⛓️ Chain Validation
- Validates block hashes, PoW, and transaction integrity.
- Ensures blocks are linked correctly to maintain chain consistency.

---

## 🏗️ Architecture Overview

### Core Entities
- **Wallet**: Holds public and private keys.
- **Transaction**: Represents value transfers between wallets.
- **Block**: Contains transactions and links to the previous block.
- **BlockchainStorage**: Manages data persistence using **LevelDB**.

### Network Design
- Decentralized network with nodes connected via gateways.
- Nodes mine blocks, validate transactions, and broadcast updates.
- Uses **Spring WebFlux** in Java and **Gorilla Mux** in Go for concurrency and routing.

---

## 📂 Project Structure

- `Context`: Manages wallets, transactions, blocks, and overall blockchain state.
- `BlockchainStorage`: Handles data persistence with LevelDB and RLP encoding.
- `Spring Controllers`: Expose blockchain functionality via HTTP.

---

## 🧬 Technical Details

### RLP Encoding
- Reduces data size and improves parsing efficiency.
- Inspired by Ethereum’s serialization method.

### Key-Value Storage
- Stores data as key-value pairs for fast querying and validation.

### Cryptography
- Built with battle-tested libraries:
  - **Java Security Module**
  - **Bouncy Castle**
  - **Guava**

### Tests
- Comprehensive testing for:
  - Signature validation
  - Hash consistency
  - Merkle root generation
  - Block linkage

---

## 🌐 Networking

To scale the local blockchain into a real network:
1. Install Spring Boot dependencies.
2. Create controllers for wallets and transactions.
3. Implement HTTP endpoints to expose blockchain functionality.

---

## 💡 Lessons Learned

- Use **RLP encoding** for efficient data handling.
- Model data in **key-value format** for faster operations.
- Leverage proven cryptographic libraries for security.
- Write tests for validation logic to ensure robustness.
- Plan for networking early to enable scalability.

---

## 📜 Roadmap

1. **Local Node** (Completed)
   - Wallet creation
   - Transaction signing
   - Block mining
   - HTTP API

2. **Networking Layer**
   - Concurrency and consistency
   - Broadcasting and syncing nodes
   - Fork resolution

3. **Scalability**
   - Support for 100+ active nodes
   - Optimized mempool and validation logic

---

## 📜 License

This project is open-source and available under the [MIT License](LICENSE).

---
![Captura de pantalla 2025-04-12 a las 18 39 39](https://github.com/user-attachments/assets/5789e7bf-85f7-4971-aa06-e562eee2f024)
![Captura de pantalla 2025-04-12 a las 18 40 04](https://github.com/user-attachments/assets/07053a0a-4985-4082-be5b-37aee38ad564)
![Captura de pantalla 2025-04-12 a las 18 40 47](https://github.com/user-attachments/assets/239ba7d7-fbe0-476d-8be8-ada9560f5da3)
![Captura de pantalla 2025-04-12 a las 18 41 38](https://github.com/user-attachments/assets/0b714581-c9ab-4df0-bb31-7643d45e090b)
![Captura de pantalla 2025-04-12 a las 18 41 53](https://github.com/user-attachments/assets/af1d0a5a-af97-4f1b-a305-a3f39ea656d8)

Happy coding! 🚀

