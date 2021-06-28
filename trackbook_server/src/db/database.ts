import Knex from "knex";
const config = require("../../knexfile");
const env = process.env.NODE_ENV || "development";

class Database {
    // @ts-ignore
    private interface: Knex = Knex(config[env]);
    private static instance: Database | undefined;

    private constructor() {}

    public isConnected() {
        return this.interface.raw('select 1');
    }

    public static getInstance(): Database {
        if(!Database.instance){
            Database.instance = new Database()
        }
        return Database.instance;
    }

    // @ts-ignore
    public getConnection(): Knex {
        return this.interface;
    }
}
const db: Database = Database.getInstance();

export default db