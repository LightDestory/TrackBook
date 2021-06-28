import {Response} from "express";
import Knex from "knex";
import db from "../db/database";
import {Sender} from "../utils/Sender";

function getUserData(id: string, response: Response): void {
    // @ts-ignore
    let con: Knex = db.getConnection();
    con.select(['readings.title', "readings.page_read", "readings.color", 'books.isbn'])
        .from('readings')
        .innerJoin('books','books.id','readings.book_id')
        .where('readings.user_id',id)
        .then((results: any[]) => {
            Sender.getInstance().sendResult(response, 200, results);
        });
}

function updateUserData(id: string, userdata: any, response: Response) {
    // @ts-ignore
    let con: Knex = db.getConnection();
    con('books')
        .select('isbn', "id")
        .then(( rows : any[]) => {
            con('readings')
                .del()
                .where("user_id", id)
                .then(() => {
                    if((Object.keys(userdata).length == 0)){
                        Sender.getInstance().sendResult(response, 200, "Library has been flushed");
                        return;
                    }
                    let ids = new Map();
                    rows.forEach((row : any) => {ids.set(row.isbn, row.id)});
                    let data = userdata.map((book: any) => ({"user_id": id, "book_id": ids.get(book.isbn), "page_read": book.page_read, "title": book.title, "color": book.color}));
                    con('readings')
                        .insert(data)
                        .then(() =>{
                            Sender.getInstance().sendResult(response, 200, "User data has been saved successfully!");

                        });
                });
        });
}

export {getUserData, updateUserData}