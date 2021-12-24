import React, { Component } from 'react'
import Book from '../Book/Book'
import './BookTable.css'

export default class BookTable extends Component {
    constructor(props) {
        super(props)

        this.state = {
            books: [],
            page: 1
        }
    }

    componentDidMount() {
        // fetch books from API
        let mockBooks = []
        for (let i = 0; i < 9; ++i) {
            mockBooks.push({
                isbn: "1111-1111-11" + i,
                title: "Book " + i,
                authors: "Author 1, Author 2, Author 3"
            })
        }

        this.setState({
            books: mockBooks
        })
    }

    render() {
        return (
            <div className='BookTable'>
                <table id='books'>
                    <thead>
                        <tr>
                            <th>ISBN</th>
                            <th>Title</th>
                            <th>Authors</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.state.books.map(b => <Book key={b.isbn} book={b} />)}
                    </tbody>
                </table>
                <div id="bookOptions">
                    <button className='brightButton'>Previous</button>
                    <h3>Page {this.state.page}</h3>
                    <button className='brightButton'>Next</button>
                </div>
            </div>
        )
    }
}
