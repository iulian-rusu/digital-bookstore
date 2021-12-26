import React, { Component } from 'react'
import BriefBook from '../Book/BriefBook'
import './BookTable.css'

export default class BookTable extends Component {
    constructor(props) {
        super(props)

        this.state = {
            page: 1,
            detailedViewIsbn: null
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
                    </>
                )
            }
        }
    }

    render() {
        const booksToDisplay = this.props.briefBooks.filter(b => b[this.props.filter.field].includes(this.props.filter.value))
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
                        {
                            booksToDisplay.map(b => <BriefBook key={b.isbn}
                                book={b}
                                userRole={this.props.user.role}
                                orderItem={this.props.orderItem}
                                removeItem={this.props.removeItem}
                                openDetails={this.props.openBookDetails} />)
                        }
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
