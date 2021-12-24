import React, { Component } from 'react'
import BriefBook from '../Book/BriefBook'
import './BookTable.css'

export default class BookTable extends Component {
    constructor(props) {
        super(props)

        this.state = {
            briefBooks: [],
            fullBooks: [],
            page: 1
        }

        this.orderBook = (isbn, q) => {
            const book = this.state.fullBooks.find(b => b.isbn === isbn)
            if (book) {
                this.props.orderItem({ ...book, quantity: q })
            }
        }

        this.renderHeading = () => {
            switch (this.props.user.role) {
                case "ROLE_USER": return (
                    <>
                        <th className='smallHeading'>Cart</th>
                        <th className='smallHeading'>Quantity</th>
                    </>
                )
                case "ROLE_MANAGER": return (
                    <>
                        <th className='smallHeading'></th>
                        <th className='smallHeading'></th>
                    </>
                )
            }
        }
    }

    componentDidMount() {
        // fetch books from API
        let mockBriefBooks = []
        for (let i = 0; i < 9; ++i) {
            mockBriefBooks.push({
                isbn: "1111-1111-11" + i,
                title: "Book " + i,
                authors: "Author " + i + ", Author " + 2 * i
            })
        }

        let mockBooks = []
        for (let i = 0; i < 9; ++i) {
            mockBooks.push({
                isbn: "1111-1111-11" + i,
                title: "Book " + i,
                authors: "Author 1, Author 2, Author 3",
                genre: "Genre " + i,
                publishYear: i,
                publisher: "Publisher " + i,
                price: 100 * i
            })
        }

        this.setState({
            briefBooks: mockBriefBooks,
            fullBooks: mockBooks
        })
    }

    render() {
        const booksToDisplay = this.state.briefBooks.filter(b => b.authors.includes(this.props.authorFilter))
        return (
            <div className='BookTable flexColumn'>
                <table id='books' className='styledTable'>
                    <thead>
                        <tr>
                            <th>ISBN</th>
                            <th>Title</th>
                            <th>Authors</th>
                            {this.renderHeading()}
                        </tr>
                    </thead>
                    <tbody>
                        {booksToDisplay.map(b => <BriefBook key={b.isbn}
                            book={b}
                            userRole={this.props.user.role}
                            orderItem={this.orderBook} />)}
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
