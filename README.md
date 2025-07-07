# Java Backtesting Engine for Trading Strategies

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/pran9v/backtesting-engine/actions)
[![Last Commit](https://img.shields.io/github/last-commit/pran9v/backtesting-engine.svg)](https://github.com/pran9v/backtesting-engine/commits/main)
[![License](https://img.shields.io/github/license/pran9v/backtesting-engine.svg)](https://github.com/pran9v/backtesting-engine/blob/main/LICENSE)
[![Java Version](https://img.shields.io/badge/java-17%2B-blue.svg)](https://openjdk.org/)

---

## 📊 Project Stats

- **Languages Used**
  - Java: 100%
- **Last Commit**: Automatically updated via GitHub badge

---

## Overview

This is a powerful and modular engine for testing trading strategies on historical stock data. Written in Java, it's designed to be realistic, extensible, and well-structured.

It acts like a **time machine** for your strategies — test ideas using past data before deploying with real money.

---

## Objective

To provide a flexible, event-driven, and extensible Java-based backtesting engine that behaves similarly to real-world trading systems.

### Key Principles

- **Real-World Simulation** – Models market data feeds, order handling, slippage, and commissions.
- **Modular Architecture** – Decouples `DataHandler`, `Strategy`, `ExecutionHandler`, and `Portfolio` components.

---

## Features

- Complete backtesting lifecycle
- Event-driven system using `MarketEvent`, `SignalEvent`, `OrderEvent`, `FillEvent`
- Realistic commission and slippage modeling
- Strategy compatibility with [TA4J](https://github.com/ta4j/ta4j)
- Performance report with total P/L and % return

---

## Limitations

- Single-asset support only
- Only MARKET orders implemented (no LIMIT, STOP, etc.)
- No risk-based position sizing
- No live broker connectivity yet
- No charts or visualizations (text-only output)

---

## Coming Soon

1. **Advanced Order Types** — LIMIT, STOP, STOP-LIMIT
2. **Risk & Portfolio Management** — Position sizing, capital allocation
3. **Live Broker Integration** — e.g., Alpaca, Zerodha Kite Connect
4. **Data Visualization** — Equity curves, trades plotted on charts
5. **Machine Learning Integration** — Use [Smile ML](https://haifengl.github.io/smile/) for strategy prediction

---

## Getting Started

### Prerequisites

- Java 17 or higher  
- Git

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/pran9v/backtesting-engine.git
   cd backtesting-engine
