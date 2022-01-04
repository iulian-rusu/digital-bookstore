import React, { Component } from 'react'
import CartOrderEntry from '../CartOrderEntry/CartOrderEntry'

export default class CartOrder extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className='CartOrder flexColumn'>
                <table id='orders' className='styledTable'>
                    <thead>
                        <tr>
                            <th>ISBN</th>
                            <th>Title</th>
                            <th>Quantity</th>
                            <th>Price</th>
                            <th className='smallHeading'></th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.props.currentOrder.map(
                            i => <CartOrderEntry key={i.isbn} item={i} removeSelf={this.props.removeOrderedItem} />
                        )}
                    </tbody>
                </table>
            </div>
        )
    }
}
