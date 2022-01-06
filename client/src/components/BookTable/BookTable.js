import React, {Component} from 'react'
import BriefBook from '../Book/BriefBook'
import './BookTable.css'

export default class BookTable extends Component {
    constructor(props) {
        super(props)

        this.state = {
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
                case "ROLE_ADMIN": return (
                    <>
                     <th className='smallHeading'></th>
                    </>
                )
            }
        }
    }

    render() {
        return (
            <div className='BookTable flexColumn'>
                <table id='books' className='styledTable'>
                    <thead>
                        <tr>
                            <th>ISBN</th>
                            <th>Title</th>
                            <th>Genre</th>
                            <th>Price</th>
                            {this.renderHeading()}
                        </tr>
                    </thead>
                    <tbody>
                        {
                            this.props.books.map(b => <BriefBook key={b.isbn}
                                book={b}
                                userRole={this.props.user.role}
                                orderItem={this.props.orderItem}
                                removeItem={this.props.removeItem}
                                openDetails={this.props.openBookDetails} />)
                        }
                    </tbody>
                </table>
                <div id="bookOptions">
                    <button className='brightButton' onClick={() => this.props.setPage(this.props.page - 1)}>Previous</button>
                    <h3>Page {this.props.page}</h3>
                    <button className='brightButton' onClick={() => this.props.setPage(this.props.page + 1)}>Next</button>
                </div>
            </div>
        )
    }
}
