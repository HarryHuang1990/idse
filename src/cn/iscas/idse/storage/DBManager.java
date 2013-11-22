package cn.iscas.idse.storage;

import java.io.File;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.evolve.Deleter;
import com.sleepycat.persist.evolve.EvolveConfig;
import com.sleepycat.persist.evolve.Mutations;

import cn.iscas.idse.config.SystemConfiguration;



/**
 * manager the Berkeley DB using to store index and other data related with the files
 * @author Harry Huang
 *
 */
public class DBManager {
	
	private Environment dbEnvironment;
	private EntityStore indexStore;
	
	public DBManager() {
		setup();
	}
	
	/**
	 * open the environment and the stores(that is, databases)
	 */
	private void setup() {
		boolean readOnly = true;
		File environmentHome = new File(SystemConfiguration.rootPath + "/database");
		try{
			 
			// Instantiate an environment configuration object
			EnvironmentConfig dbEnvConfig = new EnvironmentConfig();
			StoreConfig storeConfig = new StoreConfig();
			// Configure the environment for the read-only state as identified
			// by the readOnly parameter on this method call.
			dbEnvConfig.setReadOnly(!readOnly);
			storeConfig.setReadOnly(!readOnly);
			// If the environment is opened for write, then we want to be 
			// able to create the environment if it does not exist.
			dbEnvConfig.setAllowCreate(readOnly);
			storeConfig.setAllowCreate(readOnly);
			
			dbEnvConfig.setTransactional(readOnly);
			storeConfig.setTransactional(readOnly);
			
			// add mutations...
			Mutations mutations = new Mutations();
//			mutations.addDeleter(new Deleter("cn.iscas.idse.storage.entity.PageRankGraph", 0));
			storeConfig.setMutations(mutations);
			
			
			// Instantiate the Environment and stores. This opens them and also possibly
			// creates them.
			this.dbEnvironment = new Environment(environmentHome, dbEnvConfig);
			this.indexStore = new EntityStore(this.dbEnvironment, "Index", storeConfig);

			
		} catch(DatabaseException dbe) {
			System.err.println("Error opening environment and store: " + dbe.toString());
			System.exit(-1);
		}
	}

	/**
	 *  Getter methods: get db environment
	 * @return
	 */
	public Environment getDbEnvironment() {
		return dbEnvironment;
	}
	/**
	 *  Getter methods: get Index Store
	 * @return
	 */
	public EntityStore getIndexStore() {
		return indexStore;
	}

	/**
	 *  Close the environment and stores
	 *  stores must be closed first, then environment.
	 */
	public void close() {
		// close the stores.
		if (this.indexStore != null) {
			try {
				this.indexStore.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing store indexStore: " +
						dbe.toString());
				System.exit(-1);
			}
		}
		// close the environment.
		if (this.dbEnvironment != null) {
			try {
				this.dbEnvironment.cleanLog();
				this.dbEnvironment.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing environment" + dbe.toString());
			}
		}
	}
	
	public static void main(String[]args){
		DBManager dbm = new DBManager();
		dbm.setup();
		dbm.close();
	}
}
