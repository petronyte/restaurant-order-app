# restaurant-order-app

## Overview

**Restaurant Order App** is a desktop application built in Java using JavaFX. The project is designed to manage restaurant orders through an intuitive graphical user interface. It includes features to perform CRUD operations on clients, products, and orders (including order details) while integrating JasperReports to generate various professional reports. This system is ideal for restaurants seeking to manage orders and report on their operations efficiently.

## Features

- **Client Management:**  
  Create, update, delete, and search for clients.  
- **Product Management:**  
  Manage products by adding, modifying, deleting, and searching. A special report is provided for products priced below a threshold (e.g., below 5 units).
- **Order Management:**  
  Create orders that include selecting a client, setting a date and time, and calculating a total based on order details. Orders can be updated, searched, and deleted.
- **Master-Detail Reporting:**  
  Integrated JasperReports generate detailed reports:
  - **Restaurant Clients Report:** Displays all clients.
  - **Products Report:** Lists products with prices below 5.
  - **Orders in Preparation Report:** Shows orders currently in the preparation stage.
  - **Order Ticket Report:** A master-detail report that shows the order header (general order data) and its corresponding order details (products, quantities, prices, and subtotals).

## Technologies Used

- **Programming Language:** Java (JDK 22 or later recommended)
- **GUI Framework:** JavaFX
- **Database:** MySQL/MariaDB (with JDBC connection via MySQL Connector/J)
- **Reporting:** JasperReports and JasperSoft Studio
- **Build Tool:** Maven

## Setup and Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/restaurant-order-app.git
   ```
2. **Import the Project:**
   - Open your preferred IDE (e.g., IntelliJ IDEA or Eclipse) and import the Maven project.
3. **Database Setup:**
   - Use the provided SQL dump file (included in the repository or as shown in the documentation) to create the database schema.
   - Verify that the database connection settings in `DatabaseConnection.java` match your local MySQL/MariaDB configuration.
4. **Build the Project:**
   - Use Maven to clean and build the project:
     ```bash
     mvn clean package
     ```
5. **Run the Application:**
   - Execute the main class (e.g., `App.java`) from your IDE or from the command line:
     ```bash
     mvn exec:java -Dexec.mainClass="com.valdo.gestionpedidosrestaurante.App"
     ```

## Usage

- **Navigating the Interface:**
  - Upon launch, a main menu is presented from which you can access the Clients, Products, and Orders windows.
  - Each window has its respective CRUD functionalities.
- **Generating Reports:**
  - In the **Clients** window, click the "Generate Clients Report" button to view the restaurant clients report.
  - In the **Products** window, click the corresponding button to generate the products report (showing those with price below 5).
  - In the **Orders** window, you can generate the "Orders in Preparation" report.
  - Additionally, from the **Orders** window you can generate an order ticket (master-detail report) which combines overall order information with detailed order items.
  
## JasperReports Integration

### Report Files

- **ticketPedido.jasper:** The master report that displays general order information.
- **detallePedido.jasper:** The subreport that shows the order details (products, quantities, prices, subtotals).

Both reports must be compiled in JasperSoft Studio with proper dataset queries that use a parameter (e.g., `$P{idPedido}`) to filter the data.

### Parameter Setup in JasperSoft Studio

- In the master report, define a parameter named `idPedido` (type: java.lang.Integer).
- In the subreport, define a parameter also named `idPedido`.
- For the subreport expression in the master report, use:
  ```java
  $P{SUBREPORT_DIR} + "detallePedido.jasper"
  ```
- Although your colleague’s implementation did not require creating a `SUBREPORT_DIR` parameter, ensure that if used, it is consistently passed from the Java code.

### Code Handling

In the Java code, the application loads the Jasper files from the `src/main/resources` directory. The code sets up a parameter map including `idPedido` and (if necessary) `SUBREPORT_DIR` (using the directory path where the subreport is located). If you opt for a solution that does not use `SUBREPORT_DIR`, ensure that the subreport expression in the master report correctly locates the subreport as per your project structure.

## License

This project is licensed under the **Creative Commons Zero v1.0 Universal** license.  
You are free to download, modify, and use this software for personal and non-commercial purposes.  
Commercial use is **strictly prohibited**.

## Contributing

Feel free to fork the repository and submit pull requests. For major changes, please open an issue first to discuss what you would like to change.

## Contact

For any questions or further collaboration, please contact me at valdo.duran.p@gmail.com.
