import {Response} from "express";
import Knex from "knex";
import db from "../db/database";
import {Sender} from "../utils/Sender";
import {Book} from "../types/book.type";

export function getTitle(isbn: string, response: Response): void {
    // @ts-ignore
    let con: Knex = db.getConnection();
    con.select('title')
        .from("books")
        .where("isbn", isbn)
        .then((rows: Book[]) => {
            if (rows.length === 0) {
                Sender.getInstance().sendError(response, Sender.ERROR_TYPE_UNKNOWNBOOK);
            } else {
                Sender.getInstance().sendResult(response, 200, rows[0].title);
            }
        });
}