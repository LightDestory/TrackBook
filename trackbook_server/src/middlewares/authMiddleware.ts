import {NextFunction, Request, Response} from "express";
import Knex from "knex";
import db from "../db/database"

export function authCheck(req: Request, res: Response, next: NextFunction) {
    let auth: string | undefined = req.headers.authorization;
    if (!auth || auth.indexOf("-") == -1) {
        res.status(403).json({"status": "error", "code": "-1", "result": "Invalid Authorization header!"});
        return;
    }
    // @ts-ignore
    let con: Knex = db.getConnection();
    let token: string[] = [];
    if (auth !== undefined) {
        token = auth.split("-")
        if (isNaN(Number(token[0]))) {
            res.status(403).json({"status": "error", "code": "-1", "result": "Invalid Authorization header!"});
            return;
        }
    }
    if(req.originalUrl.includes("user")){
        if(token[0] != req.originalUrl.split("/")[-1]){
            res.status(401).json({"status": "error", "code": "-2", "result": "You don't have permission there!"});
            return;
        }
    }
    con.select()
        .from("users")
        .where('id', token[0])
        .where("password", token[1])
        .then((rows: any) => {
            if (rows.length === 0) {
                res.status(403).json({"status": "error", "code": "-1", "result": "Invalid Authorization header!"});
            } else {
                next();
            }
        });
}