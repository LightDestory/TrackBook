import {NextFunction, Request, Response} from "express";
import Knex from "knex";
import db from "../db/database"
import {Sender} from "../utils/Sender";

export function authCheck(req: Request, res: Response, next: NextFunction) {
    let auth: string | undefined = req.headers.authorization;
    if (!auth || auth.indexOf("-") == -1) {
        Sender.getInstance().sendError(res, Sender.ERROR_TYPE_UNAUTH);
        return;
    }
    let token: string[] = [];
    if(auth){
        token = auth.split("-")
        if (isNaN(Number(token[0]))) {
            Sender.getInstance().sendError(res, Sender.ERROR_TYPE_UNAUTH);
            return;
        }
    }
    // @ts-ignore
    let con: Knex = db.getConnection();
    con.select()
        .from("users")
        .where('id', token[0])
        .where("password", token[1])
        .then((rows: any[]) => {
            if (rows.length === 0) {
                Sender.getInstance().sendError(res, Sender.ERROR_TYPE_UNAUTH);
            } else {
                next();
            }
        });
}