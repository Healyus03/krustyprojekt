package krusty;

import spark.Request;
import spark.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static krusty.Jsonizer.toJson;

public class Database {
	private static final String jdbcString = "jdbc:mysql://localhost:3306/krusty?serverTimezone=UTC";
	private static final String jdbcUsername = "root";
	private static final String jdbcPassword = "uox772ln";
	private Connection connection;

	public void connect() {
		try {
			connection = DriverManager.getConnection(jdbcString, jdbcUsername, jdbcPassword);
			System.out.println("Connected to the database successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Connection failed: " + e.getMessage());
		}
	}

	public String getCustomers(Request req, Response res) {
		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT customer_name AS name, address FROM Customers")) {
			return Jsonizer.toJson(rs, "customers");
		} catch (SQLException e) {
			e.printStackTrace();
			res.status(500);
			return "{\"error\":\"Failed to retrieve customers\"}";
		}
	}

	public String getRawMaterials(Request req, Response res) {
		String query = "SELECT name, quantityInStock AS amount, unit FROM RawIngredients";
		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {
			return Jsonizer.toJson(rs, "raw-materials");
		} catch (SQLException e) {
			e.printStackTrace();
			res.status(500);
			return "{\"error\":\"Failed to retrieve raw materials\"}";
		}
	}

	public String getCookies(Request req, Response res) {
		String query = "SELECT productName AS name FROM Products";
		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {
			return Jsonizer.toJson(rs, "cookies");
		} catch (SQLException e) {
			e.printStackTrace();
			res.status(500);
			return "{\"error\":\"Failed to retrieve cookies\"}";
		}
	}

	public String getRecipes(Request req, Response res) {
		String query = "SELECT Products.productName AS cookie, RawIngredients.name AS raw_material, Ingredients.quantityInStock AS amount, RawIngredients.unit " +
				"FROM Ingredients " +
				"JOIN Products ON Ingredients.productName = Products.productName " +
				"JOIN RawIngredients ON Ingredients.ingredients_id = RawIngredients.ingredients_id";

		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {
			return Jsonizer.toJson(rs, "recipes");
		} catch (SQLException e) {
			e.printStackTrace();
			res.status(500);
			return "{\"error\":\"Failed to retrieve recipes\"}";
		}
	}

	public String getPallets(Request req, Response res) {
		StringBuilder sql = new StringBuilder(
		"SELECT pallet_id AS id, " +
				"Products.productName AS cookie, " +
				"DATE_FORMAT(Pallets.productionDate, '%Y-%m-%d') AS production_date, " +
				"'null' AS customer, " +
				"IF(Pallets.isBlocked, 'yes', 'no') AS blocked " +
				"FROM Pallets " +
				"LEFT JOIN Products ON Pallets.productName = Products.productName ");

		ArrayList<Object> values = new ArrayList<>();
		ArrayList<String> conditions = new ArrayList<>();

		if (req.queryParams("from") != null) {
			conditions.add("Pallets.productionDate >= ?");
			values.add(req.queryParams("from"));
		}
		if (req.queryParams("to") != null) {
			conditions.add("Pallets.productionDate <= ?");
			values.add(req.queryParams("to"));
		}
		if (req.queryParams("cookie") != null) {
			conditions.add("Products.productName = ?");
			values.add(req.queryParams("cookie"));
		}
		if (req.queryParams("blocked") != null) {
			conditions.add("Pallets.isBlocked = ?");
			values.add(req.queryParams("blocked").equals("yes"));
		}

		if (!conditions.isEmpty()) {
			sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
		}

		sql.append("ORDER BY Pallets.productionDate DESC");

		try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
			for (int i = 0; i < values.size(); i++) {
				if (values.get(i) instanceof String) {
					stmt.setString(i + 1, (String) values.get(i));
				} else if (values.get(i) instanceof Boolean) {
					stmt.setBoolean(i + 1, (Boolean) values.get(i));
				}
			}

			try (ResultSet rs = stmt.executeQuery()) {
				return Jsonizer.toJson(rs, "pallets");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			res.status(500);
			return "{\"error\":\"Failed to retrieve pallets\"}";
		}
	}

	public String reset(Request req, Response res) {
		try (Statement stmt = connection.createStatement()) {
			disableForeignKeyChecks(stmt);
			truncateTables(stmt);
			enableForeignKeyChecks(stmt);
			insertInitialData(stmt);
			return "{\"status\":\"ok\"}";
		} catch (SQLException e) {
			e.printStackTrace();
			res.status(500);
			return "{\"error\":\"Failed to reset database\"}";
		}
	}

	private void disableForeignKeyChecks(Statement stmt) throws SQLException {
		stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
	}

	private void enableForeignKeyChecks(Statement stmt) throws SQLException {
		stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
	}

	private void truncateTables(Statement stmt) throws SQLException {
		String[] tables = {"Request", "Orders", "Pallets_QualityCheck", "Pallets", "Ingredients", "Products", "Customers", "QualityCheck", "Truck", "RawIngredients"};
		for (String table : tables) {
			stmt.executeUpdate("TRUNCATE TABLE " + table);
		}
	}

	private void insertInitialData(Statement stmt) throws SQLException {
		// Insert into Customers
		stmt.executeUpdate("INSERT INTO Customers (customer_name, address) VALUES " +
				"('Bjudkakor AB', 'Ystad'), " +
				"('Finkakor AB', 'Helsingborg'), " +
				"('Gästkakor AB', 'Hässleholm'), " +
				"('Kaffebröd AB', 'Landskrona'), " +
				"('Kalaskakor AB', 'Trelleborg'), " +
				"('Partykakor AB', 'Kristianstad'), " +
				"('Skånekakor AB', 'Perstorp'), " +
				"('Småbröd AB', 'Malmö')");

		// Insert into Cookies (Products table)
		stmt.executeUpdate("INSERT INTO Products (productName) VALUES " +
				"('Almond delight'), " +
				"('Amneris'), " +
				"('Berliner'), " +
				"('Nut cookie'), " +
				"('Nut ring'), " +
				"('Tango')");

		// Insert into RawIngredients
		stmt.executeUpdate("INSERT INTO RawIngredients (name, quantityInStock, unit) VALUES " +
				"('Bread crumbs', 500000, 'g'), " +
				"('Butter', 500000, 'g'), " +
				"('Chocolate', 500000, 'g'), " +
				"('Chopped almonds', 500000, 'g'), " +
				"('Cinnamon', 500000, 'g'), " +
				"('Egg whites', 500000, 'ml')," +
				"('Eggs', 500000, 'g'), " +
				"('Fine-ground nuts', 500000, 'g'), " +
				"('Flour', 500000, 'g'), " +
				"('Ground, roasted nuts', 500000, 'g'), " +
				"('Icing sugar', 500000, 'g'), " +
				"('Marzipan', 500000, 'g'), " +
				"('Potato starch', 500000, 'g'), " +
				"('Roasted, chopped nuts', 500000, 'g'), " +
				"('Sodium bicarbonate', 500000, 'g'), " +
				"('Sugar', 500000, 'g'), " +
				"('Vanilla sugar', 500000, 'g'), " +
				"('Vanilla', 500000, 'g'), " +
				"('Wheat flour', 500000, 'g')");

		// Insert into Ingredients (Recipes)
		insertRecipe(stmt, "Almond delight", new String[][]{
				{"Butter", "400"},
				{"Chopped almonds", "279"},
				{"Cinnamon", "10"},
				{"Flour", "400"},
				{"Sugar", "270"}
		});

		insertRecipe(stmt, "Amneris", new String[][]{
				{"Butter", "250"},
				{"Eggs", "250"},
				{"Marzipan", "750"},
				{"Potato starch", "25"},
				{"Wheat flour", "25"}
		});

		insertRecipe(stmt, "Berliner", new String[][]{
				{"Butter", "250"},
				{"Chocolate", "50"},
				{"Eggs", "50"},
				{"Flour", "350"},
				{"Icing sugar", "100"},
				{"Vanilla sugar", "5"}
		});

		insertRecipe(stmt, "Nut cookie", new String[][]{
				{"Bread crumbs", "125"},
				{"Chocolate", "50"},
				{"Egg whites", "350"},
				{"Fine-ground nuts", "750"},
				{"Ground, roasted nuts", "625"},
				{"Sugar", "375"}
		});

		insertRecipe(stmt, "Nut ring", new String[][]{
				{"Butter", "450"},
				{"Flour", "450"},
				{"Icing sugar", "190"},
				{"Roasted, chopped nuts", "225"}
		});

		insertRecipe(stmt, "Tango", new String[][]{
				{"Butter", "200"},
				{"Flour", "300"},
				{"Sodium bicarbonate", "4"},
				{"Sugar", "250"},
				{"Vanilla", "2"}
		});
	}

	private void insertRecipe(Statement stmt, String productName, String[][] ingredients) throws SQLException {
		for (String[] ingredient : ingredients) {
			stmt.executeUpdate("INSERT INTO Ingredients (productName, ingredients_id, quantityInStock) VALUES " +
					"('" + productName + "', (SELECT ingredients_id FROM RawIngredients WHERE name = '" + ingredient[0] + "'), " + ingredient[1] + ")");
		}
	}

	public String createPallet(Request req, Response res) {
		String cookie = req.queryParams("cookie");
		if (cookie == null || cookie.isEmpty()) {
			res.status(400);
			return "{\"status\":\"unknown cookie\"}";
		}

		try {
			// Check if the cookie exists
			String checkCookieQuery = "SELECT productName FROM Products WHERE productName = ?";
			try (PreparedStatement checkStmt = connection.prepareStatement(checkCookieQuery)) {
				checkStmt.setString(1, cookie);
				try (ResultSet rs = checkStmt.executeQuery()) {
					if (!rs.next()) {
						res.status(400);
						return "{\"status\":\"unknown cookie\"}";
					}
				}
			}

			// Insert a new pallet
			String insertPalletQuery = "INSERT INTO Pallets (productName, productionDate, isBlocked) VALUES (?, NOW(), FALSE)";
			try (PreparedStatement insertStmt = connection.prepareStatement(insertPalletQuery, Statement.RETURN_GENERATED_KEYS)) {
				insertStmt.setString(1, cookie);
				int rowsAffected = insertStmt.executeUpdate();
				if (rowsAffected == 0) {
					res.status(500);
					return "{\"status\":\"error\"}";
				}

				// Get the generated pallet ID
				try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int palletId = generatedKeys.getInt(1);

						// Update raw materials according to the recipe
						String updateRawMaterialsQuery = "UPDATE RawIngredients ri " +
								"JOIN Ingredients i ON ri.ingredients_id = i.ingredients_id " +
								"SET ri.quantityInStock = ri.quantityInStock - (i.quantityInStock * 54) " +
								"WHERE i.productName = ?";
						try (PreparedStatement updateStmt = connection.prepareStatement(updateRawMaterialsQuery)) {
							updateStmt.setString(1, cookie);
							updateStmt.executeUpdate();
						}

						return "{\"status\":\"ok\", \"id\": " + palletId + "}";
					} else {
						res.status(500);
						return "{\"status\":\"error\"}";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			res.status(500);
			return "{\"status\":\"error\"}";
		}
	}
}