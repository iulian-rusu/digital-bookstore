import React, {Component} from 'react'
import BookTable from '../BookTable/BookTable'
import './BooksPage.css'
import DetailedBook from '../DetailedBook/DetailedBook'

export default class BooksPage extends Component {

    constructor(props) {
        super(props)

        this.state = {
            page: 1,
            itemsPerPage: 4,
            books: [],
            searchField: "",
            filter: {
                field: "title",
                value: ""
            }
        }

        this.setSearchField = e => {
            this.setState({
                searchField: e.target.value
            })
        }

        this.setFilter = () => {
            this.state.filter.value = this.state.searchField
            this.loadPage()
        }

        this.clearFilter = () => {
            this.state.filter.value = ""
            this.state.searchField = ""
            this.loadPage()
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

            if (answer.ok) {
                console.log(answer.statusText)
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
            if (newPage > this.state.page && this.state.books.length < this.state.itemsPerPage) {
                return
            }
            if (newPage > 0) {
                this.state.page = newPage
                await this.loadPage()
            }
        }

        this.loadPage = async () => {
            const filter = this.state.filter
            const query = `page=${this.state.page}&items_per_page=${this.state.itemsPerPage}&${filter.field}=${filter.value}`
            const uri = `http://localhost:8080/api/book-library/books?${query}`
            const answer = await fetch(uri)
            if (!answer.ok) {
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
            this.setState({
                books: bookList,
            })
        }

        this.renderPage = () => {
            if (this.state.detailedViewIsbn) {
                const book = this.state.books.filter(b => b.isbn == this.state.detailedViewIsbn)[0]
                return (
                    <>
                        <DetailedBook book={book} userRole={this.props.user.role} user={this.props.user} />
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
                            <option>title</option>
                            <option>genre</option>
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
