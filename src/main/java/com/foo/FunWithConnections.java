package com.foo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class FunWithConnections {
	
	private Integer numberOfConnections;
	
	private DataSource dataSource;
	
	
	public FunWithConnections(GenericObjectPoolConfig<PoolableConnection> config, int howManyConnectionDoYouWantToCreate) {
		this.numberOfConnections = howManyConnectionDoYouWantToCreate;
		
		System.out.println("Loading JDBC driver.");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("Setting up data source.");
		dataSource = setupDataSource(config);
	}
	
	public void run() throws SQLException {

		List<Connection> connList = new ArrayList<>();
		connList = createSomeConnections(this.dataSource, this.numberOfConnections);
		closeConnections(connList);
	}

	private List<Connection> createSomeConnections(DataSource dataSource, int connectionNumber) 
			throws SQLException {
		
		List<Connection> connections = new ArrayList<>();
		for (int i = 0; i < connectionNumber; i++) {
			System.out.println("Creating connection " + i);
			connections.add(dataSource.getConnection());
		}
		return connections;
		
	}

	@SuppressWarnings("unused")
	private void delayThread() {
		try {
			TimeUnit.SECONDS.sleep(1);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void closeConnections(List<Connection> connectionList) {
		int i = 0;
		for (Connection c : connectionList) {
			
			try {

				if (c != null) {
					System.out.println("Closing connection " + i++);
					c.close();
				}
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}

	private DataSource setupDataSource(GenericObjectPoolConfig<PoolableConnection> config) {

		Properties connectionProperties = new Properties();
		connectionProperties.put("user", "root");
		connectionProperties.put("password", "secret3!");
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				"jdbc:mysql://localhost/matcha?useSSL=false", connectionProperties);
		
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
		
		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory, config);
		
		poolableConnectionFactory.setPool(connectionPool);
		PoolingDataSource<PoolableConnection> dataSource = new PoolingDataSource<>(connectionPool);

		return dataSource;
	}
}