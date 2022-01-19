import React, {Component} from 'react'
import './DetailedBook.css'

export default class DetailedBook extends Component {
    constructor(props) {
        super(props)

        this.state = {
            ...this.props.book,
            authors: ""
        }

        this.submitForm = async event => {
            event.preventDefault()
            const uri = `http://localhost:8080/api/book-library/books/${this.state.isbn}`
            const res = await fetch(uri, {
                method: 'put',
                headers: {
                    'Authorization': `Bearer ${this.props.user.token}`,
                    'Content-type': 'application/json'
                },
                body: JSON.stringify(this.state)
            })

            if (!res.ok) {
                console.log(res.statusText)
            }
        }

        this.updateBook = field => event => {
            this.setState({
                [field]: event.target.value
            })
        }
    }

    async componentDidMount() {
        let book = this.state
        const uri = `http://localhost:8080/api/book-library/books/${book.isbn}/authors`
        const answer = await fetch(uri)
        if (!answer.ok) {
            console.log(answer.statusText)
            return
        }
        const responseData = await answer.json()
        if (responseData["_embedded"] === undefined) {
            book["authors"] = "-"
            return
        }
        const authors = responseData['_embedded']['authorList']
        let authorString = ""
        for (const author of authors) {
            authorString += author.firstName + " " + author.lastName + "; "
        }
        this.setState({ authors: authorString })
    }

    render() {
        const isDisabled = this.props.userRole != "ROLE_MANAGER"
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
                                type="text" disabled value={this.state.isbn} />
                        </div>
                        <div>
                            <label>Authors</label>
                            <input type="text" disabled value={this.state.authors} />
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
                                type="text" disabled={isDisabled} value={'$' + this.state.price} />
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
