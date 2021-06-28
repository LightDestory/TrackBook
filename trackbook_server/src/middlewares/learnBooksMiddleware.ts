import {NextFunction, Request, Response} from "express";
import Knex from "knex";
import db from "../db/database"

export function learnBooks(req: Request, res: Response, next: NextFunction) {
    if(req.method == "GET" || (Object.keys(req.body).length == 0)) {
        next();
    } else {
        // @ts-ignore
        let con: Knex = db.getConnection();
        let data = req.body;
        con('books')
            .select("isbn")
            .then((rows : any[]) => {
                let library = rows.map((row : any) => {return row.isbn});
                let user_isbns = data.map((book : any) => { return book.isbn;});
                let unknown_books = user_isbns.filter((book : string) => !library.includes(book));
                if(unknown_books.length == 0) {
                    next();
                } else {
                    let newdata = data.filter((book: any) => unknown_books.includes(book.isbn));
                    con('books')
                        .insert(newdata.map((book: any) => ({"isbn": book.isbn, "title": book.title}) ))
                        .then(() => {
                            next();
                        });
                }
            });
    }
}