import React, { Component } from 'react'
import BookTable from '../BookTable/BookTable'
import './BooksPage.css'
import DetailedBook from '../DetailedBook/DetailedBook'

export default class BooksPage extends Component {

    constructor(props) {
        super(props)

        this.state = {
            briefBooks: [],
            detailedBooks: [],
            searchField: "",
            filter: {
                field: "authors",
                value: ""
            }
        }

        this.setSearchField = e => {
            this.setState({
                searchField: e.target.value
            })
        }

        this.setFilter = () => {
            this.setState({
                filter: {
                    field: this.state.filter.field,
                    value: this.state.searchField
                },
            })
        }

        this.clearFilter = () => {
            this.setState({
                filter: {
                    field: this.state.filter.field,
                    value: ""
                },
                searchField: ""
            })
        }

        this.setField = event => {
            this.setState({
                filter: {
                    field: event.target.value,
                    value: this.state.filter.value
                }
            })
        }

        this.openBookDetails = isbn => {
            this.setState({
                detailedViewIsbn: isbn
            })
        }

        this.removeBook = isbn => {
            // call DELETE method to remove book
            this.setState({
                briefBooks: this.state.briefBooks.filter(b => b.isbn !== isbn)
            })
        }

        this.orderBook = (isbn, q) => {
            const book = this.state.detailedBooks.find(b => b.isbn === isbn)
            if (book) {
                this.props.orderItem({ ...book, quantity: q })
            }
        }

        this.renderPage = () => {
            if (this.state.detailedViewIsbn) {
                return (
                    <>
                        <DetailedBook isbn={this.state.detailedViewIsbn} userRole={this.props.user.role} />
                        <button className="brightButton" onClick={() => this.setState({
                            detailedViewIsbn: null
                        })}>Back</button>
                    </>
                )
            }
            return (
                <>
                    <div id='searchDiv'>
                        <select id="filterType" onChange={this.setField}>
                            <option>authors</option>
                            <option>title</option>
                        </select>
                        <input type='text'
                            id='searchInput'
                            placeholder={'search ' + this.state.filter.field}
                            onChange={this.setSearchField}
                            value={this.state.searchField} />
                        <button className='brightButton' id='searchButton' onClick={this.setFilter}>Search</button>
                        <button className='brightButton' id='searchButton' onClick={this.clearFilter}>Clear</button>
                    </div>
                    <BookTable
                        briefBooks={this.state.briefBooks}
                        openBookDetails={this.openBookDetails}
                        user={this.props.user}
                        orderItem={this.orderBook}
                        removeItem={this.removeBook}
                        filter={this.state.filter}
                    />
                </>
            )
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
                price: 100 * i,
                stock: 33 * i + 1
            })
        }

        this.setState({
            briefBooks: mockBriefBooks,
            detailedBooks: mockBooks
        })
    }

    render() {


        return (
            <div className='BooksPage flexColumn alignCenter'>
                {this.renderPage()}
            </div>
        )
    }
}
