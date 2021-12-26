import React, { Component } from 'react'
import './DetailedBook.css'

export default class DetailedBook extends Component {
    constructor(props) {
        super(props)

        this.state = {
            isbn: "1111-1111-543",
            title: "Book ",
            authors: "Author 1, Author 2, Author 3",
            genre: "Genre ",
            publishYear: 353242,
            publisher: "Publisher ",
            price: 4242,
            stock: 42
        }
        this.submitForm = event => {
            event.preventDefault()
            // call API to update book
        }

        this.updateBook = field => event => {
            this.setState({
                [field]: event.target.value
            })
        }
    }

    render() {
        const isDisabled = this.props.userRole == "ROLE_USER"
        return (
            <div className='DetailedBook flexColumn alignCenter'>
                <form id="bookViewForm" className="flexColumn alignCenter" onSubmit={this.submitForm}>
                    <fieldset className="flexColumn alignCenter">
                        <legend>Detailed Book Information</legend>
                        <div>
                            <label>Title</label>
                            <input onChange={this.updateBook('title')}
                                type="text" disabled={isDisabled} value={this.state.title} />
                        </div>
                        <div>
                            <label>ISBN</label>
                            <input onChange={this.updateBook('isbn')}
                                type="text" disabled={isDisabled} value={this.state.isbn} />
                        </div>
                        <div>
                            <label>Authors</label>
                            <input onChange={this.updateBook('authors')}
                                type="text" disabled={isDisabled} value={this.state.authors} />
                        </div>
                        <div>
                            <label>Genre</label>
                            <input onChange={this.updateBook('genre')}
                                type="text" disabled={isDisabled} value={this.state.genre} />
                        </div>
                        <div>
                            <label>Year</label>
                            <input onChange={this.updateBook('publishYear')}
                                type="text" disabled={isDisabled} value={this.state.publishYear} />
                        </div>
                        <div>
                            <label>Publisher</label>
                            <input onChange={this.updateBook('publisher')}
                                type="text" disabled={isDisabled} value={this.state.publisher} />
                        </div>
                        <div>
                            <label>Price</label>
                            <input onChange={this.updateBook('price')}
                                type="text" disabled={isDisabled} value={this.state.price} />
                        </div>
                        <div>
                            <label>Stock</label>
                            <input onChange={this.updateBook('stock')}
                                type="text" disabled={isDisabled} value={this.state.stock} />
                        </div>
                    </fieldset>
                    {
                        this.props.userRole === 'ROLE_MANAGER' ?
                            <div>
                                <input type="submit" className='darkButton' value="Update" />
                            </div> : null
                    }
                </form>
            </div>
        )
    }
}
