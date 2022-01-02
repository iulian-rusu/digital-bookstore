import React, { Component } from 'react'
import BookTable from '../BookTable/BookTable'
import './BooksPage.css'
import DetailedBook from '../DetailedBook/DetailedBook'

export default class BooksPage extends Component {

    constructor(props) {
        super(props)

        this.state = {
            page: 1,
            books: [],
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

        this.removeBook = async isbn => {
            const uri = `http://localhost:8080/api/book-library/books/${isbn}`
            const answer = await fetch(uri, {
                method: 'delete',
                headers: {
                    'Authorization': `Bearer ${this.props.user.token}`
                }
            })

            if (answer.status != 204) {
                return
            }
            this.setState({
                books: this.state.books.filter(b => b.isbn !== isbn)
            })
        }

        this.orderBook = (isbn, q) => {
            const book = this.state.books.find(b => b.isbn === isbn)
            if (book) {
                this.props.orderItem({ ...book, quantity: q })
            }
        }


        this.setPage = async newPage => {
            if (newPage > 0) {
                this.state.page = newPage
                await this.loadPage()
            }
        }

        this.loadPage = async () => {
            const uri = `http://localhost:8080/api/book-library/books?page=${this.state.page}`
            const answer = await fetch(uri)
            if (answer.status != 200) {
                this.setState({
                    books: []
                })
                return
            }

            const responseData = await answer.json()
            if (responseData["_embedded"] === undefined) {
                this.setState({
                    books: []
                })
                return
            }
                
            const bookList = responseData['_embedded']['bookList']

            for(let i = 0; i < bookList.length; ++i) {
                let book = bookList[i]
                const uri = `http://localhost:8080/api/book-library/books/${book.isbn}/authors`
                const answer = await fetch(uri)
                if (answer.status != 200) {
                    continue
                }
                const responseData = await answer.json()
                if (responseData["_embedded"] === undefined) {
                    book["authors"] = ""
                    continue
                }

                const authors = responseData['_embedded']['bookAuthorList']
                let authorString = ""
                for(let j = 0; j < authors.length; ++j) {
                    const uri = `http://localhost:8080/api/book-library/authors/${authors[j].authorId}`
                    const answer = await fetch(uri)
                    if (answer.status != 200) {
                        continue
                    }
                    const responseData = await answer.json()
                    authorString += responseData.firstName + " " + responseData.lastName + "; "
                }
                book["authors"] = authorString
            }
            console.log(bookList)
            this.setState({
                books: bookList,
            })
        }

        this.renderPage = () => {
            if (this.state.detailedViewIsbn) {
                const book = this.state.books.filter(b => b.isbn == this.state.detailedViewIsbn)[0]
                return (
                    <>
                        <DetailedBook book={book} userRole={this.props.user.role} user={this.props.user}/>
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
                        page={this.state.page}
                        setPage={this.setPage}
                        books={this.state.books}
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

    async componentDidMount() {
        await this.loadPage()
    }

    render() {
        return (
            <div className='BooksPage flexColumn alignCenter'>
                {this.renderPage()}
            </div>
        )
    }
}
